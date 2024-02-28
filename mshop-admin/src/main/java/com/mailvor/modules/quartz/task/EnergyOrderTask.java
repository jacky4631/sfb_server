package com.mailvor.modules.quartz.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.modules.energy.domain.UserEnergyOrder;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderService;
import com.mailvor.modules.push.service.JPushService;
import com.mailvor.modules.quartz.utils.OrderTaskUtil;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.tk.vo.GoodsDetailVo;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 热度订单定时任务，根据热度值生成订单，需要从订单池中获取订单
 * 生成淘客最终订单
 * @author Zheng Jie
 * @date 2023-02-04
 */
@Slf4j
@Component
public class EnergyOrderTask {

    public static SimpleDateFormat sdf =new SimpleDateFormat("yyMMdd");
    @Resource
    protected MailvorTbOrderService tbOrderService;
    @Resource
    protected MailvorJdOrderService jdOrderService;
    @Resource
    protected MailvorPddOrderService pddOrderService;
    @Resource
    protected MailvorDyOrderService dyOrderService;
    @Resource
    protected MailvorVipOrderService vipOrderService;

    @Resource
    private JPushService jPushService;

    @Resource
    private UserEnergyOrderService energyOrderService;

    @Resource
    private UserEnergyOrderLogService orderLogService;
    @Resource
    private OrderTaskUtil orderTaskUtil;
    @Resource
    private MwSystemUserLevelService userLevelService;
    @Resource
    private MwSystemConfigService systemConfigService;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    protected void run(String paramStr) {
        List<String> platforms = JSON.parseArray(paramStr, String.class);
        //找到releaseTime时间已到，同时没有发放的记录
        List<UserEnergyOrder> energyOrders = energyOrderService.getUnpaidList(platforms);
        if(energyOrders.isEmpty()){
            return;
        }


        List<MailvorTbOrder> tbOrders = new ArrayList<>();
        List<MailvorJdOrder> jdOrders = new ArrayList<>();
        List<MailvorPddOrder> pddOrders = new ArrayList<>();
        List<MailvorDyOrder> dyOrders = new ArrayList<>();
        List<MailvorVipOrder> vipOrders = new ArrayList<>();
        List<UserEnergyOrder> updateEnergyOrders = new ArrayList<>();

        Map<String, MwSystemUserLevel> levelMap = userLevelService.getSystemLevels(Arrays.asList(5))
                .stream().collect(Collectors.toMap(MwSystemUserLevel::getType, Function.identity()));

        Double min = Double.parseDouble(systemConfigService.getData(SystemConfigConstants.TK_HB_MIN_TIMES));
        Double max = Double.parseDouble(systemConfigService.getData(SystemConfigConstants.TK_HB_MAX_TIMES));


        List<GoodsDetailVo> detailVos = null;
        JSONArray jdDetailVos = null;
        JSONArray pddDetailVos = null;
        JSONArray dyDetailVos = null;
        JSONArray vipDetailVos = null;
        List<Long> logIdList = energyOrders.stream().map(energyOrder -> energyOrder.getLogId()).distinct().collect(Collectors.toList());

        List<UserEnergyOrderLog> logList = orderLogService.listByIds(logIdList);
        Map<Long, UserEnergyOrderLog> logMap = logList.stream().collect(Collectors.toMap(UserEnergyOrderLog::getId, Function.identity()));

        List<Long> energyUids = new ArrayList<>();
        List<Long> expUids = new ArrayList<>();


        for(UserEnergyOrder energyOrder : energyOrders) {

            Long uid = energyOrder.getUid();
            Date time = energyOrder.getReleaseTime();
            String platform = energyOrder.getPlatform();
            //2=热度订单 1=体验订单
            Integer innerType = 2;
            UserEnergyOrderLog orderLog = logMap.get(energyOrder.getLogId());
            if(orderLog != null && orderLog.getType() == 2) {
                innerType = 1;
                expUids.add(uid);
            } else {
                energyUids.add(uid);
            }


            //因为会员等级一级分佣比例为20%，获取当前平台
            BigDecimal discountOne = orderTaskUtil.getDiscountOne(levelMap, platform);

            BigDecimal newHb = OrderUtil.getRoundFee(NumberUtil.mul(NumberUtil.div(energyOrder.getReleaseMoney(), discountOne), 100));
            double fee = newHb.doubleValue();

            double minFee = fee*min;
            double maxFee = fee*max;


            //根据平台生成订单
            switch (platform){
                case "jd":
                    if(CollectionUtils.isEmpty(jdDetailVos)) {
                        jdDetailVos = orderTaskUtil.getGoodsListJd();
                    }
                    JSONObject jdDetail = orderTaskUtil.getGoodsJd(jdDetailVos, minFee, maxFee);
                    if(jdDetail == null) {
                        continue;
                    }
                    String jdOrderId = "2" + DateUtils.randomInt(4, 6) + orderTaskUtil.getOrderId(10);
                    energyOrder.setOrderId(jdOrderId);
                    energyOrder.setPaid(1);
                    updateEnergyOrders.add(energyOrder);
                    double commission = jdDetail.getDouble("actualPrice")*jdDetail.getDouble("commissionShare")/100;
                    MailvorJdOrder jdOrder = jdOrderService.mockOrder(Long.parseLong(jdOrderId), jdDetail.getLong("skuId"),
                            uid, time,
                            jdDetail.getString("shopName"), jdDetail.getDouble("commissionShare"),
                            NumberUtil.round(commission, 2).doubleValue(),
                            jdDetail.getDouble("actualPrice"), jdDetail.getString("picMain"),
                            jdDetail.getString("skuName"), innerType);
                    jdOrders.add(jdOrder);
                    break;
                case "pdd":
                //生成拼多多模拟订单
                    if(CollectionUtils.isEmpty(pddDetailVos)) {
                        pddDetailVos = orderTaskUtil.getGoodsListPdd();
                    }
                    JSONObject pddDetail = orderTaskUtil.getGoodsPdd(pddDetailVos, minFee, maxFee);
                    if(pddDetail == null) {
                        continue;
                    }

                    String pddOrderId = sdf.format(time) + "-" + orderTaskUtil.getOrderId(2) + orderTaskUtil.getOrderId(13);
                    energyOrder.setOrderId(pddOrderId);
                    energyOrder.setPaid(1);
                    updateEnergyOrders.add(energyOrder);
                    double jdCommission = pddDetail.getDouble("minGroupPrice")*pddDetail.getDouble("promotionRate")/100;
                    MailvorPddOrder pddOrder = pddOrderService.mockOrder(pddOrderId, pddDetail.getString("goodsSign"),
                            uid, time,
                            pddDetail.getString("mallName"),
                            pddDetail.getDouble("promotionRate"),
                            NumberUtil.round(jdCommission, 2).doubleValue(),
                            pddDetail.getDouble("minGroupPrice"),
                            pddDetail.getString("goodsImageUrl"), pddDetail.getString("goodsName"), innerType);
                    pddOrders.add(pddOrder);
                    break;
                case  "dy":
                //生成模拟订单
                    if(CollectionUtils.isEmpty(dyDetailVos)) {
                        dyDetailVos = orderTaskUtil.getGoodsListDy();
                    }
                    JSONObject dyDetail = orderTaskUtil.getGoodsDy(dyDetailVos, minFee, maxFee);
                    if(dyDetail == null) {
                        continue;
                    }
                    String dyOrderId = System.currentTimeMillis()*3 + "" + orderTaskUtil.getOrderId(6);
                    energyOrder.setOrderId(dyOrderId);
                    energyOrder.setPaid(1);
                    updateEnergyOrders.add(energyOrder);
                    MailvorDyOrder dyOrder = dyOrderService.mockOrder(dyOrderId, dyDetail.getString("productId"),
                            uid, time,
                            dyDetail.getString("shopName"), dyDetail.getDouble("cosRatio"),
                            dyDetail.getDouble("cosFee"),
                            dyDetail.getDouble("price"),
                            dyDetail.getString("cover"), dyDetail.getString("title"), innerType);
                    dyOrders.add(dyOrder);
                    break;
                case "vip":
                //生成模拟订单
                    if(CollectionUtils.isEmpty(vipDetailVos)) {
                        vipDetailVos = orderTaskUtil.getGoodsListVip();
                    }
                    JSONObject vipDetail = orderTaskUtil.getGoodsVip(vipDetailVos, minFee, maxFee);
                    if(vipDetail == null) {
                        continue;
                    }
                    String vipOrderId = sdf.format(time) + orderTaskUtil.getOrderId(8);
                    energyOrder.setOrderId(vipOrderId.toString());
                    energyOrder.setPaid(1);
                    updateEnergyOrders.add(energyOrder);
                    MailvorVipOrder vipOrder = vipOrderService.mockOrder(vipOrderId, uid, time,
                            vipDetail.getString("brandName"),
                            Double.parseDouble(vipDetail.getString("commissionRate")),
                            Double.parseDouble(vipDetail.getString("commission")),
                            Double.parseDouble(vipDetail.getString("vipPrice")),
                            vipDetail.getString("goodsThumbUrl"),
                            vipDetail.getString("goodsName"), vipDetail.getString("goodsId"), innerType);
                    vipOrders.add(vipOrder);
                    break;
                case "tb":
                //生成模拟订单
                    if(CollectionUtils.isEmpty(detailVos)) {
                        detailVos = orderTaskUtil.getGoodsList();
                    }
                    GoodsDetailVo tbDetail = orderTaskUtil.getGoods(detailVos, minFee, maxFee);
                    if(tbDetail == null) {
                        continue;
                    }
                    String tbOrderId = "3" + DateUtils.randomInt(0, 1) + orderTaskUtil.getOrderId(4) + System.currentTimeMillis();
                    energyOrder.setOrderId(tbOrderId.toString());
                    energyOrder.setPaid(1);
                    updateEnergyOrders.add(energyOrder);
                    BigDecimal tbFee = NumberUtil.round(NumberUtil.div(
                            NumberUtil.mul(tbDetail.getActualPrice(), tbDetail.getCommissionRate()), 100), 2);
                    MailvorTbOrder tbOrder = tbOrderService.mockOrder(Long.parseLong(tbOrderId), uid, time,
                            tbDetail.getShopName(), tbDetail.getCommissionRate(), tbFee.doubleValue(),
                            tbDetail.getActualPrice(),
                            tbDetail.getMainPic(), tbDetail.getTitle(), tbDetail.getItemLink(), tbDetail.getGoodsId(), innerType);
                    tbOrders.add(tbOrder);
                    break;
            }
        }

        try {
            if(!updateEnergyOrders.isEmpty()) {
                //更新订单号
                energyOrderService.mysqlInsertOrUpdateBath(updateEnergyOrders);
                //保存订单
                if(!tbOrders.isEmpty()) {
                    tbOrderService.saveBatch(tbOrders);
                }
                if(!jdOrders.isEmpty()) {
                    jdOrderService.saveBatch(jdOrders);
                }
                if(!pddOrders.isEmpty()) {
                    pddOrderService.saveBatch(pddOrders);
                }
                if(!dyOrders.isEmpty()) {
                    dyOrderService.saveBatch(dyOrders);
                }
                if(!vipOrders.isEmpty()) {
                    vipOrderService.saveBatch(vipOrders);
                }

                //触发刷新每日预估等信息
                List<Long> uidList = updateEnergyOrders.stream().map(UserEnergyOrder::getUid).distinct().collect(Collectors.toList());
                RedisUtil.setFeeUid(uidList.toArray(new Long[0]));
                jPushService.push("有新的热度订单，进订单中心查看", energyUids);
                jPushService.push("有新的体验订单，进订单中心查看", expUids);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
