package com.mailvor.modules.quartz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.dataoke.dto.OrderListGetResponseOrderListDTO;
import com.mailvor.modules.meituan.MeituanService;
import com.mailvor.modules.meituan.config.MeituanConfig;
import com.mailvor.modules.meituan.param.MeituanOrderParam;
import com.mailvor.modules.meituan.utils.MeituanUtil;
import com.mailvor.modules.tk.config.JdConfig;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.tk.param.*;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.tk.vo.*;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.VipOrderDetailDto;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.StringUtils;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mailvor.modules.meituan.constants.MeituanConstants.MT_MEDIUM_PREFIX;

/**
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Slf4j
@Component
public class OrderTask {
    protected static DateTimeFormatter FF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Resource
    protected MailvorTbOrderService orderService;

    @Resource
    protected MailvorJdOrderService jdOrderService;
    @Resource
    protected MailvorPddOrderService pddOrderService;

    @Resource
    protected MailvorVipOrderService vipOrderService;
    @Resource
    protected MailvorDyOrderService dyOrderService;

    @Resource
    protected MailvorMtOrderService mtOrderService;

    @Resource
    protected MailvorEleOrderService eleOrderService;
    @Resource
    protected DataokeService dataokeService;

    @Resource
    protected PddService pddService;

    @Resource
    protected KuService kuService;

    @Resource
    private JdConfig jdConfig;

    @Resource
    protected IGenerator generator;

    @Resource
    private MwUserUnionService userUnionService;

    @Resource
    private MeituanService meituanService;
    @Resource
    private MeituanConfig meituanConfig;

    protected String saveTb(QueryTBParam param) {
        TBResVo res = dataokeService.queryTBList(param);
        if(res != null && res.getData() != null && res.getData().getResults() != null) {
            List<MailvorTbOrder> orders = res.getData().getResults().getPublisher_order_dto();
            if(CollectionUtils.isEmpty(orders)) {
                return null;
            }
            try {
                //获取订单的淘宝渠道id
                List<String> pids = orders.stream()
                        .filter(order-> order.getRelationId() != null && order.getRelationId() > 0)
                        .map(order -> order.getRelationId().toString()).distinct().collect(Collectors.toList());
                //获取用户uid
                Map<String, Long> userUnionMap;
                if(pids.isEmpty()) {
                    userUnionMap = new HashMap<>();
                } else {
                    userUnionMap = userUnionService.listByTbPid(pids).stream()
                            .collect(Collectors.toMap(MwUserUnion::getTbPid, MwUserUnion::getUid));
                }

                //交换父子订单编号，因为同一店铺下单多个商品，父订单号相同，导致只同步一个订单，
                orders.stream().forEach(order -> {
                    order.setParentId(order.getTradeParentId());
                    order.setTradeParentId(Long.parseLong(order.getTradeId()));
                    //淘宝渠道id 绑定用户
                    if(order.getRelationId() != null) {
                        Long uid = userUnionMap.get(order.getRelationId().toString());
                        if(uid != null) {
                            order.setUid(uid);
                            //设置需要刷新今日预估的用户uid
                            RedisUtil.setFeeUid(uid);
                        }
                    }
                });

                boolean saved = orderService.saveOrUpdateBatch(orders);

                log.debug(saved? "淘宝订单保存成功": "淘宝订单保存失败");
                return res.getData().getPosition_index();
            }catch (Exception e) {
                e.printStackTrace();
                log.error("淘宝订单保存失败：{}", e);
            }
        }

        return null;
    }


    /**
     * 增加事务读已提交Read Committed 防止京店订单orderId重复导致的死锁
     * 读数据时只能读到已经提交了的数据 （没有脏读）
     * 写数据时只能覆盖已经提交了的数据 （没有脏写）
     * */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    protected Integer saveJd(QueryJdParam param) {
        param.setKey(jdConfig.getKey());
        JdResVo res = dataokeService.queryJdList(param);
        if(res != null && res.getData() != null && res.getData().getList() != null && !res.getData().getList().isEmpty()) {
            ArrayList<MailvorJdOrder> orders = res.getData().getList();
            //过滤父订单
            Map<Long, Long> parentIdMap = new HashMap<>();

            orders.stream().forEach(order -> {
                if(order.getParentId() != 0) {
                    parentIdMap.put(order.getParentId(), order.getParentId());
                }
                //京东skuid为空导致拆红包异常，暂时生成时间戳
                if(order.getSkuId() == null) {
                    order.setSkuId(System.currentTimeMillis());
                }
                //绑定用户
                Long uid = OrderUtil.getJdOrderUser(order.getPositionId());
                if(uid > 0) {
                    order.setUid(uid);
                    //设置需要刷新今日预估的用户uid
                    RedisUtil.setFeeUid(uid);
                }
            });
            List<MailvorJdOrder> filterOrders = orders.stream().filter(mailvorJdOrder -> {
                Long orderId = mailvorJdOrder.getOrderId();
                return !parentIdMap.containsKey(orderId);
            }).collect(Collectors.toList());

            try {
                //某些情况
                boolean saved = jdOrderService.saveOrUpdateBatch(filterOrders);
                log.debug(saved? "京东订单保存成功": "京东订单保存失败");
            }catch (Exception e) {
                log.error("京东订单保存失败 {}", e);
            }
        }
        if(res != null && res.getData() != null) {
            return res.getData().getHasMore();
        }
        return 0;
    }

    protected boolean saveDy(QueryDyParam param) {
        DyResVo res = dataokeService.queryDyList(param);
        if(res != null && res.getData() != null && res.getData().getList() != null && !res.getData().getList().isEmpty()) {
            ArrayList<MailvorDyOrder> orders = res.getData().getList();
            orders.forEach(dyOrder -> {
                //773026_8_0200 中间的是用户id
                Long uid = OrderUtil.getDyOrderUser(dyOrder.getExternalInfo());
                if(uid > 0) {
                    dyOrder.setUid(uid);
                    //设置需要刷新今日预估的用户uid
                    RedisUtil.setFeeUid(uid);
                }
            });
            boolean saved = dyOrderService.saveOrUpdateBatch(orders);

            log.debug(saved? "抖音订单保存成功": "抖音订单保存失败");
            return res.getData().getTotal() > param.getPage()*param.getSize();
        }

        return false;
    }


    protected Integer saveDyKu(QueryDyKuParam param) {
        DyKuResVo res = null;
        try {
            res = kuService.dyLifeOrder(param);
        }catch (Exception e) {
            log.error("保存抖音库本地生活订单报错：{}", e);
            return 0;
        }
        if(res != null && res.getCode() == 200 && !CollectionUtils.isEmpty(res.getData())) {
            ArrayList<MailvorDyKuOrder> kuOrders = res.getData();
            List<MailvorDyOrder> orders = kuOrders.stream().map(dyKuOrder-> dyKuOrder.convert()).collect(Collectors.toList());
            orders.forEach(dyOrder -> {
                //773026_8_0200 中间的是用户id
                Long uid = OrderUtil.getDyOrderUser(dyOrder.getExternalInfo());
                if(uid > 0) {
                    dyOrder.setUid(uid);
                    //设置需要刷新今日预估的用户uid
                    RedisUtil.setFeeUid(uid);
                }
            });
            boolean saved = dyOrderService.saveOrUpdateBatch(orders);

            log.debug(saved? "抖音库订单保存成功": "抖音库订单保存失败");
            return res.getMinId();
        }

        return 0;
    }

    protected String savePdd(QueryPddParam param) {
        PddDdkOrderListRangeGetResponse res = pddService.queryPddOrderList(param);
        if(res != null
                && res.getOrderListGetResponse() != null
                && res.getOrderListGetResponse().getOrderList() != null
                && !res.getOrderListGetResponse().getOrderList().isEmpty()) {
            List<PddDdkOrderListRangeGetResponse.OrderListGetResponseOrderListItem> orders =
                    res.getOrderListGetResponse().getOrderList();
            //todo 由于拼多多订单时间为秒，存入数据库后显示1970年，目前手动乘以1000 后期优化
            List<OrderListGetResponseOrderListDTO> orderDTOS = generator.convert(orders, OrderListGetResponseOrderListDTO.class);
            orderDTOS = orderDTOS.stream().map(o->{
                if(o.getOrderCreateTime() != null) {
                    o.setOrderCreateTime(o.getOrderCreateTime()*1000);
                }
                if(o.getOrderGroupSuccessTime() != null) {
                    o.setOrderGroupSuccessTime(o.getOrderGroupSuccessTime()*1000);
                }
                if(o.getOrderModifyAt() != null) {
                    o.setOrderModifyAt(o.getOrderModifyAt()*1000);
                }
                if(o.getOrderPayTime() != null) {
                    o.setOrderPayTime(o.getOrderPayTime()*1000);
                }
                if(o.getOrderReceiveTime() != null) {
                    o.setOrderReceiveTime(o.getOrderReceiveTime()*1000);
                }
                if(o.getOrderSettleTime() != null) {
                    o.setOrderSettleTime(o.getOrderSettleTime()*1000);
                }
                if(o.getOrderVerifyTime() != null) {
                    o.setOrderVerifyTime(o.getOrderVerifyTime()*1000);
                }
                return o;
            }).collect(Collectors.toList());
            List<MailvorPddOrder> pddOrders = generator.convert(orderDTOS, MailvorPddOrder.class);
            //订单绑定用户
            pddOrders.forEach(pddOrder -> {
                //拼多多授权后customParams格式为{"uid":"55"}，未授权为pid，解析报错
                String customParams = pddOrder.getCustomParameters();
                if(StringUtils.isBlank(customParams)) {
                    return;
                }
                try{
                    JSONObject obj = JSON.parseObject(customParams);
                    Long uid = Long.parseLong(obj.getString("uid"));
                    if(uid != null && uid > 0) {
                        pddOrder.setUid(uid);
                        //设置需要刷新今日预估的用户uid
                        RedisUtil.setFeeUid(uid);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });
            boolean saved = pddOrderService.saveOrUpdateBatch(pddOrders);
            log.debug(saved? "拼多多订单保存成功": "拼多多订单保存失败");
            return res.getOrderListGetResponse().getLastOrderId();

        }
        return null;

    }

    protected boolean saveVip(QueryVipParam param) {
        VipResVo res = dataokeService.queryVipList(param);
        if(res != null && res.getData() != null && res.getData().getOrderInfoList() != null && !res.getData().getOrderInfoList().isEmpty()) {
            VipDataVo data = res.getData();
            ArrayList<MailvorVipOrder> orders = data.getOrderInfoList();
            //绑定用户
            orders.forEach(vipOrder -> {
                Long uid = OrderUtil.getVipOrderUser(vipOrder.getStatParam());
                if(uid > 0) {
                    vipOrder.setUid(uid);
                    //设置需要刷新今日预估的用户uid
                    RedisUtil.setFeeUid(uid);
                }
                if(!CollectionUtils.isEmpty(vipOrder.getDetailList())) {
                    VipOrderDetailDto vipOrderDetailDto = JSON.parseObject(JSON.toJSONString(vipOrder.getDetailList().get(0)), VipOrderDetailDto.class);
                    Double rate = Double.parseDouble(vipOrderDetailDto.getCommissionRate());
                    vipOrder.setCommissionRate(rate);
                }
            });
            boolean saved = vipOrderService.saveOrUpdateBatch(orders);
            log.debug(saved? "唯品会订单保存成功": "唯品会订单保存失败");
            return data.getTotal() > data.getPageSize() * data.getPage();

        }

        return false;
    }



    private MeituanOrderParam initParam(QueryMtParam param) {
        MeituanOrderParam objParam = new MeituanOrderParam();
        objParam.setScrollId(param.getScrollId());
        if(param.getSize() != null) {
            objParam.setLimit(param.getSize());
        }
//        if(param.getQueryType() != null) {
//            objParam.put("queryType", param.getQueryType());
//        }
        if(param.getStartTime() != null) {
            objParam.setStartTime(param.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond());
        }

        if(param.getEndTime() != null) {
            objParam.setEndTime(param.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond());
        }
        if(param.getPlatform() != null) {
            objParam.setPlatform(param.getPlatform());
        }
        if(!CollectionUtils.isEmpty(param.getBusinessLine())) {
            objParam.setBusinessLine(param.getBusinessLine());
        }
        objParam.setScrollId(param.getScrollId());
        return objParam;
    }

    private String saveMtOrder(MtResVo res) {
        if(res != null && res.getCode() != null && res.getCode()==200
                && res.getMsg() != null
                && !CollectionUtils.isEmpty(res.getMsg().getRecords())) {
            List<MailvorMtOrder> orders = res.getMsg().getRecords();
            orders = orders.stream().filter(order -> {
                //cps和cpa订单有重复，然后cpatype为-1的订单佣金不正常，暂时过滤掉，暂未找到原因
                if(order.getCpaType() != null && order.getCpaType() == -1) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            if(orders.isEmpty()) {
                return res.getMsg().getPositionIndex();
            }
            //订单绑定用户
            orders.forEach(order -> {
                if(order.getUniqueItemId()==null) {
                    if(order.getItemId() != null) {
                        order.setUniqueItemId(order.getItemId());
                    } else {
                        order.setUniqueItemId(order.getOrderId());
                    }
                }
                //美团uid解析
                String medium = order.getUtmMedium();
                if(StringUtils.isBlank(medium)) {
                    return;
                }
                try{
                    String dec = MeituanUtil.decryptHex(medium, meituanConfig.getAppKey());
                    if(!dec.startsWith(MT_MEDIUM_PREFIX)) {
                        return;
                    }
                    Long uid = Long.parseLong(dec.replace(MT_MEDIUM_PREFIX, ""));
                    if(uid != null && uid > 0) {
                        order.setUid(uid);
                        //设置需要刷新今日预估的用户uid
                        RedisUtil.setFeeUid(uid);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });
            boolean saved = mtOrderService.saveOrUpdateBatch(orders);
            log.debug(saved? "美团订单保存成功": "美团订单保存失败");
            return res.getMsg().getPositionIndex();

        }
        return null;
    }

    protected String saveMtCPS(QueryMtParam param) {
        MeituanOrderParam objParam = initParam(param);
        MtResVo res = meituanService.order(objParam);
        saveMtOrder(res);
        return res != null ? res.getMsg().getPositionIndex() : null;
    }


    protected Integer saveEleKu(QueryEleKuParam param) {
        EleKuResVo res = kuService.eleOrder(param);
        if(res != null && res.getCode() == 200 && !CollectionUtils.isEmpty(res.getData())) {
            ArrayList<MailvorEleKuOrder> eleOrders = res.getData();
            List<MailvorEleOrder> orders = eleOrders.stream().map(dyKuOrder-> dyKuOrder.convert()).collect(Collectors.toList());
            orders.forEach(dyOrder -> {
                if(StringUtils.isNotBlank(dyOrder.getChannelCode())) {
                    try {
                        Long uid = Long.parseLong(dyOrder.getChannelCode());
                        if(uid > 0) {
                            dyOrder.setUid(uid);
                            //设置需要刷新今日预估的用户uid
                            RedisUtil.setFeeUid(uid);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            boolean saved = eleOrderService.saveOrUpdateBatch(orders);

            log.debug(saved? "饿了么库订单保存成功": "饿了么库订单保存失败");
            return res.getMinId();
        }

        return 0;
    }
}
