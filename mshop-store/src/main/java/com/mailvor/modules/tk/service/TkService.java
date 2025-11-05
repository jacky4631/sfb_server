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
import com.mailvor.modules.tk.param.GoodsListDyParam;
import com.mailvor.modules.tk.param.GoodsListPddParam;
import com.mailvor.modules.tk.param.ParseContentParam;
import com.mailvor.modules.tk.param.jd.GoodsListJDParam;
import com.mailvor.modules.tk.service.mapper.TkOrderMapper;
import com.mailvor.modules.tk.util.ClipboardParseUtil;
import com.mailvor.modules.tk.util.HttpUtil;
import com.mailvor.modules.tk.util.LinkUtil;
import com.mailvor.modules.tk.util.VipUtil;
import com.mailvor.modules.tk.vo.*;
import com.mailvor.modules.tk.vo.jd.JdKuGoodsDetailVO;
import com.mailvor.modules.tk.vo.jd.JdKuSearchListVO;
import com.mailvor.modules.tk.vo.pdd.PddSearchListVO;
import com.mailvor.modules.tk.vo.pdd.PddSearchVO;
import com.mailvor.modules.tk.vo.vip.VipGoodsDetailDataVo;
import com.mailvor.modules.tk.vo.vip.VipGoodsDetailVO;
import com.mailvor.modules.tk.vo.vip.VipWordCodeVO;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.RedisUtil;
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
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

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


    public TkParseCodeVO mixParse(ParseContentParam param, MwUser mwUser) {
        String content = param.getContent();
        TkParseCodeVO res = new TkParseCodeVO();
        //当查券内容长度大于1200 中止查券
        if(content.length() > ClipboardParseUtil.getContentLimit()) {
            return res;
        }
        Matcher excludeMatcher = ClipboardParseUtil.getExcludePattern().matcher(content);
        if(excludeMatcher.find()) {
            return res;
        }
        boolean parseAgain = false;
        if(ClipboardParseUtil.getTbPattern().matcher(content).find()) {
            String pid = null;
            String channelId = null;
            MwUserUnion userUnion = null;
            if(mwUser != null) {
                userUnion = userUnionService.getOne(mwUser.getUid());
            }
            //获取pid为了实现自动追单
            if(userUnion != null && StringUtils.isNotBlank(userUnion.getTbPid())) {
                //如果渠道id存在使用渠道id，并使用渠道pid
                pid = tbConfig.getChannelPid();
                channelId = userUnion.getTbPid();
            }
            if(channelId == null && mwUser != null) {
                pid = RedisUtil.getPid(mwUser.getUid(), null);
            }

            DataokeResVo<GoodsParseVo> resVo = dataokeService.goodsParse(content, pid, channelId);
            if(resVo.getCode() == 0) {
                res.setCode(0);

                //优先根据标题使用联盟搜索
//                List<TkGoodsVO> tkGoodsVOS = tbSearch(content);
                res.setData(LinkUtil.parseDtkGoodsParse(resVo.getData(), content));
                log.info("查券返回结果 {}", JSON.toJSONString(res));
                return res;
            } else {
                parseAgain = true;
            }
        } else if (ClipboardParseUtil.getPddPattern().matcher(content).find()) {
            //拼多多链接使用大淘客的列表查询接口
            GoodsListPddParam pddParam = new GoodsListPddParam();
            pddParam.setKeyword(content);
            PddSearchListVO pddSearchListVO = dataokeService.goodsListPdd(pddParam);
            if(pddSearchListVO != null && pddSearchListVO.getData() != null
                    && org.apache.commons.collections.CollectionUtils.isNotEmpty(pddSearchListVO.getData().getList())) {
                PddSearchVO pddSearchVO = pddSearchListVO.getData().getList().get(0);
                res.setCode(0);
                res.setData(LinkUtil.parsePddDetail(pddSearchVO, content));
                log.info("查券返回结果 {}", JSON.toJSONString(res));
                return res;
            }
        } else if (ClipboardParseUtil.getJdPattern().matcher(content).find()) {
            GoodsListJDParam jdParam = new GoodsListJDParam();
            jdParam.setKeyword(content);
            JdKuSearchListVO jdKuSearchListVO = kuService.searchJD(jdParam);
            if(jdKuSearchListVO.getCode()==200 && jdKuSearchListVO.getData().size() > 0) {
                JdKuGoodsDetailVO jdKuGoodsDetailVO = jdKuSearchListVO.getData().get(0);
                res.setCode(0);
                res.setData(LinkUtil.parseJdKuDetail(jdKuGoodsDetailVO, content));
                log.info("查券返回结果 {}", JSON.toJSONString(res));
                return res;
            } else {
                parseAgain = true;
            }
        } else if(ClipboardParseUtil.getDyPattern().matcher(content).find()) {
            GoodsListDyParam dyParam = new GoodsListDyParam();
            try {
                dyParam.setKeyword(URLEncoder.encode(content, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            dyParam.setSort(0);
            dyParam.setPageId(1);
            dyParam.setPageSize(20);

//            dyParam.setKeyword(content);
            DySearchListVO dySearchListVO = kuService.dyProductList(dyParam);
            if(dySearchListVO.getCode() == 200 && org.apache.commons.collections.CollectionUtils.isNotEmpty(dySearchListVO.getData())) {
                res.setCode(0);
                DySearchVO dySearchVO = dySearchListVO.getData().get(0);
                res.setData(LinkUtil.parseDyKuCmsDetail(dySearchVO, dySearchVO.getGoodsId(), content, ""));
            } else {
                res.setCode(-1);
                TkParseVO data = new TkParseVO();
                data.setOriginContent(content);
                res.setData(data);
            }
        } else if(ClipboardParseUtil.getVipPattern().matcher(content).find()) {
            String longUrl = LinkUtil.covertToVipLongUrl(content);
            String goodsId = LinkUtil.parseVipLink(longUrl);
            if(goodsId != null) {
                String openId = VipUtil.getVipShopOpenId(mwUser != null ? mwUser.getUid() : null);
                VipGoodsDetailDataVo vipRes = dataokeService.goodsDetailVIP(goodsId, openId);
                if(vipRes != null && vipRes.getGoods() != null) {
                    VipGoodsDetailVO vipDetail = vipRes.getGoods();
                    res.setCode(0);
                    String buyLink = "";
                    try {
                        VipWordCodeVO vipWords = dataokeService.goodsWordVIP(vipDetail.getDestUrl(),
                                openId,
                                TkUtil.getVipGenRequest(openId, vipDetail.getAdCode()));
                        buyLink = vipWords.getWord().getUrl();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    res.setData(LinkUtil.parseVipKuDetail(vipDetail, goodsId, content, buyLink));
                }
            } else {
                res.setCode(-1);
                TkParseVO data = new TkParseVO();
                data.setOriginContent(content);
                res.setData(data);
            }
        }
        if(parseAgain) {
            //使用好单库替换
            res = kuService.clipboard(param);

            if(res != null && res.getCode() == 0) {
                res.getData().setOriginContent(content);
            } else {
                res.setCode(-1);
                TkParseVO data = new TkParseVO();
                data.setOriginContent(content);
                res.setData(data);
            }
        }
        if(res.getData() == null) {
            res.setData(TkParseVO.builder().originContent(content).build());
        }
        log.info("查券返回结果 {}", JSON.toJSONString(res));
        return res;
    }

    @Transactional
    public void submitOrder(String origOrderId, Long uid, boolean checkBinding) throws ExecutionException, InterruptedException {
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
                if(checkBinding) {
                    checkBinding(tbOrder.getBind(), uid, tbOrder.getUid());
                }
                suStoreOrderService.bindOrder(uid, tbOrder);
            }
            //保存用户追单号
            mwUserService.updateAdditionalNo(uid, DateUtils.getAdditionalNo(orderId));
        } else if (!CollectionUtils.isEmpty(jdOrders)) {
            for(MailvorJdOrder jdOrder: jdOrders) {
                if(checkBinding) {
                    checkBinding(jdOrder.getBind(), uid, jdOrder.getUid());
                }
                suStoreOrderService.bindOrder(uid, jdOrder);
            }
        } else if (pddOrder != null) {
            if(checkBinding) {
                checkBinding(pddOrder.getBind(), uid, pddOrder.getUid());
            }
            suStoreOrderService.bindOrder(uid, pddOrder);
        } else if (vipOrder != null) {
            if(checkBinding) {
                checkBinding(vipOrder.getBind(), uid, vipOrder.getUid());
            }
            suStoreOrderService.bindOrder(uid, vipOrder);
        } else if (dyOrder != null) {
            if(checkBinding) {
                checkBinding(dyOrder.getBind(), uid, dyOrder.getUid());
            }
            suStoreOrderService.bindOrder(uid, dyOrder);
        } else if (!CollectionUtils.isEmpty(mtOrders)) {
            for(MailvorMtOrder mtOrder1: mtOrders) {
                if(checkBinding) {
                    checkBinding(mtOrder1.getBind(), uid, mtOrder1.getUid());
                }
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
