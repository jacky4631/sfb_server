package com.mailvor.modules.tk.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.api.MshopException;
import com.mailvor.modules.order.service.SuStoreOrderService;
import com.mailvor.modules.tk.config.JdConfig;
import com.mailvor.modules.tk.config.PddConfig;
import com.mailvor.modules.tk.config.TbConfig;
import com.mailvor.modules.tk.domain.*;
import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.service.mapper.TkOrderMapper;
import com.mailvor.modules.tk.util.HttpUtil;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.StringUtils;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgVegasTljCreateRequest;
import com.taobao.api.request.TbkDgVegasTljReportRequest;
import com.taobao.api.response.TbkDgVegasTljCreateResponse;
import com.taobao.api.response.TbkDgVegasTljReportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mailvor.utils.DateUtils.YYYY_MM_DD;

/**
 * @projectName:openapi
 * @author:
 * @createTime: 2022/11/11 14:55
 * @description:
 */
@Slf4j
@Component
public class TkService {
    @Resource
    private SuStoreOrderService suStoreOrderService;
    @Resource
    private DataokeService dataokeService;
    @Resource
    private PddService pddService;
    @Resource
    private TbConfig tbConfig;

    @Resource
    private PddConfig pddConfig;

    @Resource
    private JdConfig jdConfig;
    @Resource
    private MailvorTbOrderService tbOrderService;

    @Resource
    private MailvorJdOrderService jdOrderService;

    @Resource
    private MailvorPddOrderService pddOrderService;

    @Resource
    private MailvorVipOrderService vipOrderService;

    @Resource
    protected MwUserService mwUserService;
    @Resource
    protected MwUserUnionService userUnionService;
    @Resource
    private MailvorDyOrderService dyOrderService;
    @Resource
    private MailvorMtOrderService mtOrderService;

    @Resource
    private KuService kuService;

    @Resource
    private TkOrderMapper orderMapper;

    @Value("${tb.pid.excludes}")
    private List<String> excludePids;


    public JSONObject mixParse(ParseContentParam param, MwUser mwUser) throws UnsupportedEncodingException {
        JSONObject res = new JSONObject();
        //当查券内容长度大于800 中止查券
        String content = param.getContent();
        if(content.length() > 800) {
            return res;
        }
        if(content.contains("douyin.com") || content.contains("ɗΌƱϔίǸ") || content.contains("ɖʘȗɏΊɧ")) {
            JSONObject dyObj = kuService.contentParse(content);
            log.info("抖音查券结果 {}", dyObj.toJSONString());
            Object data = dyObj.get("data");
            if(data != null) {
                if(data instanceof JSONObject) {
                    String goodsId = dyObj.getJSONObject("data").getString("goods_id");
                    if(goodsId !=null){
                        JSONObject dyGoodsDetail = dataokeService.dyGoodsDetail(goodsId);
                        if(dyGoodsDetail.getJSONObject("data") != null
                                && !dyGoodsDetail.getJSONObject("data").getJSONArray("list").isEmpty()){
                            JSONObject dyDetail = dyGoodsDetail.getJSONObject("data").getJSONArray("list").getJSONObject(0);
                            res.put("code", 0);
                            String dyPassword = dyObj.getJSONObject("data").getString("dyPassword");
                            res.put("data", HttpUtil.parseDyDetail(dyDetail, goodsId, content,dyPassword));
                        }
                    }
                }
            }

        } else if(content.contains("vip.com") || content.contains("m.vipglobal.hk")) {
            String goodsId = HttpUtil.parseVipLink(param.getContent());
            String openId = TkUtil.getVipOpenId(mwUser);
            JSONObject vipRes = dataokeService.goodsDetailVIP(goodsId, openId);
            if(vipRes != null && vipRes.getJSONArray("data") != null && !vipRes.getJSONArray("data").isEmpty()) {
                JSONObject vipDetail = vipRes.getJSONArray("data").getJSONObject(0);
                res.put("code", 0);
                String buyLink = "";
                try {
                    JSONObject vipWords = dataokeService.goodsWordVIP(vipDetail.getString("destUrl"),
                            openId,
                            TkUtil.getVipGenRequest(openId, vipDetail.getString("adCode")));
                    buyLink = vipWords.getJSONObject("data").getJSONArray("urlInfoList").getJSONObject(0).getString("url");
                }catch (Exception e) {
                    e.printStackTrace();
                }
                res.put("data", HttpUtil.parseVipDetail(vipDetail, goodsId, content, buyLink));
            }
        } else {
            if(mwUser != null) {
                MwUserUnion userUnion = userUnionService.getOne(mwUser.getUid());
                if(userUnion !=null && StringUtils.isNotBlank(userUnion.getTbPid())) {
                    param.setTbPid(tbConfig.getChannelPid());
                    param.setTbChannelId(userUnion.getTbPid());
                }
                //只有授权=1才需要传customParam，
                int auth = pddService.authQuery(mwUser.getUid());
                if(auth == 1) {
                    param.setCustomerParameters(pddConfig.getParam(mwUser.getUid()));
                }
                param.setPddPid(pddConfig.getPid());

                param.setJdUnionId(jdConfig.getUnionId());

                param.setJdPositionId(mwUser.getUid().toString());

                log.debug("jd mix parse positionId {}", param.getJdPositionId());
            }
//            res = dataokeService.parseContent(param);
            //使用好单库替换
            JSONObject kuRes = kuService.clipboard(param);
            log.info("好单裤查券内容 {} 返回结果 {}", content, kuRes.toJSONString());
            //把好单库结构转换成大淘客，保持接口一致
            res = ku2Da(kuRes, content);
        }
        if(res != null && res.getJSONObject("data") != null && res.getJSONObject("data").get("platType") !=null) {
            String platType = res.getJSONObject("data").getString("platType");
            if("taobao".equals(platType)) {
                res.getJSONObject("data").put("platType", "tb");
            } else if("pdd".equals(platType)) {
                res.getJSONObject("data").put("parseStatus", 0);
            }
        }
        return res;
    }

    public JSONObject mixParse2(ParseContentParam param, MwUser mwUser) {
        JSONObject res = new JSONObject();
        //当查券内容长度大于800 中止查券
        String content = param.getContent();
        if(content.length() > 800) {
            return res;
        }
        log.info("查券内容 {}", content);
        if(content.contains("零撸") || content.contains("提现")) {
            return res;
        }
        if(content.contains("vip.com") || content.contains("m.vipglobal.hk")) {
            String goodsId = HttpUtil.parseVipLink(param.getContent());
            String openId = TkUtil.getVipOpenId(mwUser);
            JSONObject vipRes = dataokeService.goodsDetailVIP(goodsId, openId);
            if(vipRes != null && vipRes.get("data") != null && (vipRes.get("data") instanceof JSONArray) &&
                    !vipRes.getJSONArray("data").isEmpty()) {
                JSONObject vipDetail = vipRes.getJSONArray("data").getJSONObject(0);
                res.put("code", 200);
                String buyLink = "";
                try {
                    JSONObject vipWords = dataokeService.goodsWordVIP(vipDetail.getString("destUrl"),
                            openId,
                            TkUtil.getVipGenRequest(openId, vipDetail.getString("adCode")));
                    buyLink = vipWords.getJSONObject("data").getJSONArray("urlInfoList").getJSONObject(0).getString("url");
                }catch (Exception e) {
                    e.printStackTrace();
                }
                res.put("data", HttpUtil.parseVipKuDetail(vipDetail, goodsId, content, buyLink));
            }
        } else {
            //使用好单库替换
            res = kuService.clipboard(param);

            if(res != null && res.getInteger("code") == 200) {
                res.getJSONObject("data").put("originContent", content);
            } else {
                if(res == null) {
                    res = new JSONObject();
                }
                JSONObject data = new JSONObject();
                data.put("originContent", content);
                res.put("data", data);
            }
        }
        log.info("查券返回结果 {}", res.toJSONString());
        return res;
    }
    @Transactional
    public void submitOrder(String origOrderId, Long uid) throws ExecutionException, InterruptedException {
        log.info("用户{} 提交订单号 {}", uid, origOrderId);
        //美团订单有空格 需要清除
        String orderId = origOrderId.replace(" ", "");
        if(!ReUtil.isMatch("^[0-9-]{10,30}$", orderId)){
            throw new MshopException("不是正确的订单号");
        }
        CompletableFuture<List<MailvorTbOrder>> tbOrderFuture =
                CompletableFuture.supplyAsync(()->
                        tbOrderService.list(new LambdaQueryWrapper<MailvorTbOrder>()
                                .and(i->i.eq(MailvorTbOrder::getParentId,orderId)
                                        .or()
                                        .eq(MailvorTbOrder::getTradeParentId,orderId))));
        CompletableFuture<List<MailvorJdOrder>> jdOrderFuture = CompletableFuture.supplyAsync(()->{
            LambdaQueryWrapper<MailvorJdOrder> wrapperO = new LambdaQueryWrapper<>();
            wrapperO.eq(MailvorJdOrder::getOrderId, orderId);
            return jdOrderService.list(wrapperO);
        });
        CompletableFuture<MailvorPddOrder> pddOrderFuture = CompletableFuture.supplyAsync(()->pddOrderService.getById(orderId));
        CompletableFuture<MailvorVipOrder> vipOrderFuture = CompletableFuture.supplyAsync(()->vipOrderService.getById(orderId));
        CompletableFuture<MailvorDyOrder> dyOrderFuture = CompletableFuture.supplyAsync(()->dyOrderService.getById(orderId));
        CompletableFuture<List<MailvorMtOrder>> mtOrder2Future = CompletableFuture.supplyAsync(()->mtOrderService
                .list(Wrappers.<MailvorMtOrder>lambdaQuery().eq(MailvorMtOrder::getOrderId,orderId)));
        CompletableFuture.allOf(tbOrderFuture, jdOrderFuture, pddOrderFuture, vipOrderFuture, dyOrderFuture, mtOrder2Future);


        List<MailvorTbOrder> tbOrders = tbOrderFuture.get();
        List<MailvorJdOrder> jdOrders = jdOrderFuture.get();
        MailvorPddOrder pddOrder = pddOrderFuture.get();
        MailvorVipOrder vipOrder = vipOrderFuture.get();
        MailvorDyOrder dyOrder = dyOrderFuture.get();
        List<MailvorMtOrder> mtOrders = mtOrder2Future.get();

        if (CollectionUtils.isEmpty(tbOrders) && CollectionUtils.isEmpty(jdOrders) && ObjectUtil.isNull(pddOrder) &&
                ObjectUtil.isNull(vipOrder) && ObjectUtil.isNull(dyOrder) && CollectionUtils.isEmpty(mtOrders)) {
            throw new MshopException("订单不存在");
        }
        if(!CollectionUtils.isEmpty(tbOrders)) {
            for(MailvorTbOrder tbOrder : tbOrders) {
                if(excludePids.contains(tbOrder.getAdzoneId().toString())){
                    throw new MshopException("订单不存在");
                }
                checkBinding(tbOrder.getBind(), uid, tbOrder.getUid());
                suStoreOrderService.bindOrder(uid, tbOrder);
            }
            //保存用户追单号
            mwUserService.updateAdditionalNo(uid, DateUtils.getAdditionalNo(orderId));
        } else if (!CollectionUtils.isEmpty(jdOrders)) {
            for(MailvorJdOrder jdOrder: jdOrders) {
                checkBinding(jdOrder.getBind(), uid, jdOrder.getUid());
                suStoreOrderService.bindOrder(uid, jdOrder);
            }
        } else if (pddOrder != null) {
            checkBinding(pddOrder.getBind(), uid, pddOrder.getUid());
            suStoreOrderService.bindOrder(uid, pddOrder);
        } else if (vipOrder != null) {
            checkBinding(vipOrder.getBind(), uid, vipOrder.getUid());
            suStoreOrderService.bindOrder(uid, vipOrder);
        } else if (dyOrder != null) {
            checkBinding(dyOrder.getBind(), uid, dyOrder.getUid());
            suStoreOrderService.bindOrder(uid, dyOrder);
        } else if (!CollectionUtils.isEmpty(mtOrders)) {
            for(MailvorMtOrder mtOrder1: mtOrders) {
                checkBinding(mtOrder1.getBind(), uid, mtOrder1.getUid());
                suStoreOrderService.bindOrder(uid, mtOrder1);
            }
        }
    }


    protected void checkBinding(Integer bind, Long loginUid, Long orderUid) {
        if(orderUid != null && orderUid > 0) {
            if(loginUid.equals(orderUid)) {
                throw new MshopException("订单已绑定，请勿重复提交");
            } else {
                throw new MshopException("订单已被其他用户绑定，如有疑问，请联系客服");
            }
        }
        if(bind == 2 || bind == 3) {
            throw new MshopException("订单已失效");
        }
    }

    protected JSONObject ku2Da(JSONObject kuObj, String origContent) {
        Integer code = kuObj.getInteger("code");
        JSONObject daObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        daObj.put("status",200);
        daObj.put("msg","请求成功");
        daObj.put("data",dataObj);
        dataObj.put("originContent", origContent);
        if(code == 200) {
            JSONObject data = kuObj.getJSONObject("data");
            String startPriceStr = data.getString("item_price");
            if(StringUtils.isNotBlank(startPriceStr)) {
                //查券成功;
                dataObj.put("itemId", data.getString("item_id"));
                dataObj.put("itemName", data.getString("item_title"));
                dataObj.put("mainPic", data.getString("item_pic"));

                Double startPrice = Double.parseDouble(startPriceStr);
                dataObj.put("originalPrice", startPrice);
                String endPriceStr = data.getString("item_end_price");
                Double endPrice = StringUtils.isBlank(endPriceStr) ? startPrice : Double.parseDouble(endPriceStr);
                dataObj.put("actualPrice", endPrice);
                dataObj.put("couponPrice", NumberUtil.round(startPrice - endPrice, 2));
                dataObj.put("parseStatus", 3);
                //描述：1.淘宝  2.京东  3.拼多多  4.抖音
                Integer platType = data.getInteger("plat_type");
                String platTypeStr = "taobao";
                if(platType == 2) {
                    platTypeStr = "jd";
                } else if (platType == 3) {
                    platTypeStr = "pdd";
                }
                dataObj.put("platType", platTypeStr);
                dataObj.put("shopName", data.getString("shop_name"));
                Double rates = Double.parseDouble(data.getString("rates"));
                dataObj.put("commissionRate", rates);
                dataObj.put("commissionAmount", NumberUtil.round(rates*endPrice/100, 2));
                return daObj;
            }
        }
        dataObj.put("parseStatus", 0);
        return daObj;
    }

    /**
     * 计算某个用户所有订单表的订单数量
     * */
    public Long orderCount(Long uid, Integer innerType) {
        return orderMapper.orderCount(uid, innerType);
    }

    public JSONObject createTlj(String goodsId, Double tljMoney) throws ApiException {
        // create Client
        TaobaoClient client = new DefaultTaobaoClient(tbConfig.getUrl(), tbConfig.getAppKey(), tbConfig.getAppSecret());
        TbkDgVegasTljCreateRequest req = new TbkDgVegasTljCreateRequest();
        req.setSecurityLevel(0L);
        req.setUseStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        req.setUseEndTimeMode(1L);
        req.setUseEndTime("1");
        req.setSendEndTime(DateUtil.endOfDay(new Date()));
        req.setSendStartTime(DateUtil.beginOfDay(new Date()));
        req.setPerFace(tljMoney.toString());
        req.setSecuritySwitch(true);
        req.setUserTotalWinNumLimit(1L);
        req.setName("淘礼金来啦");
        req.setTotalNum(1L);
        req.setItemId(goodsId);
        req.setCampaignType("MKT");
        req.setAdzoneId(tbConfig.getAdZoneId());
        TbkDgVegasTljCreateResponse rsp = client.execute(req);
        log.info("淘礼金返回:{}", rsp.getBody());
        return JSON.parseObject(rsp.getBody());
    }
    public JSONObject getTljUse(String tljId) throws ApiException {
        // create Client
        TaobaoClient client = new DefaultTaobaoClient(tbConfig.getUrl(), tbConfig.getAppKey(), tbConfig.getAppSecret());
        TbkDgVegasTljReportRequest req = new TbkDgVegasTljReportRequest();
        req.setRightsId(tljId);
        req.setAdzoneId(tbConfig.getAdZoneId());
        TbkDgVegasTljReportResponse rsp = client.execute(req);
        log.info("淘礼金状态返回:{}", rsp.getBody());
        return JSON.parseObject(rsp.getBody());
    }

}
