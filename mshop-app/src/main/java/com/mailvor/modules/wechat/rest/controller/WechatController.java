/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.ApiResult;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.enums.AfterSalesStatusEnum;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.enums.PayMethodEnum;
import com.mailvor.modules.mp.config.WxMaConfiguration;
import com.mailvor.modules.mp.config.WxMpConfiguration;
import com.mailvor.modules.mp.config.WxPayConfiguration;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.sales.domain.StoreAfterSales;
import com.mailvor.modules.sales.service.StoreAfterSalesService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName WechatController
 * @author huangyu
 * @Date 2019/11/5
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "微信模块", tags = "微信:微信模块")
public class WechatController {

    private final MwStoreOrderService orderService;
    private final MwSystemConfigService systemConfigService;
    private final MwUserRechargeService userRechargeService;
    private final StoreAfterSalesService storeAfterSalesService;

    private final RestTemplate restTemplate;

    private final RedisUtils redisUtils;
    private final MwUserService userService;
    /**
     * 微信分享配置
     */
    @GetMapping("/share")
    @ApiOperation(value = "微信分享配置",notes = "微信分享配置")
    public ApiResult<Map<String,Object>> share() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("img",systemConfigService.getData(SystemConfigConstants.WECHAT_SHARE_IMG));
        map.put("title",systemConfigService.getData(SystemConfigConstants.WECHAT_SHARE_TITLE));
        map.put("synopsis",systemConfigService.getData(SystemConfigConstants.WECHAT_SHARE_SYNOPSIS));
        Map<String,Object> mapt = new LinkedHashMap<>();
        mapt.put("data",map);
        return ApiResult.ok(mapt);
    }

    /**
     * jssdk配置
     */
    @GetMapping("/wechat/config")
    @ApiOperation(value = "jssdk配置",notes = "jssdk配置")
    public ApiResult<Map<String,Object>> jsConfig(HttpServletRequest request) throws WxErrorException {
        WxMpService wxService = WxMpConfiguration.getWxMpService();
        String url = request.getParameter("url");
        log.info("url:"+url);
        WxJsapiSignature jsapiSignature = wxService.createJsapiSignature(url);
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("appId",jsapiSignature.getAppId());
        map.put("jsApiList",new String[]{"updateAppMessageShareData","openLocation","scanQRCode",
                "chooseWXPay","updateAppMessageShareData","updateTimelineShareData",
                "openAddress","editAddress","getLocation"});
        map.put("nonceStr",jsapiSignature.getNonceStr());
        map.put("signature",jsapiSignature.getSignature());
        map.put("timestamp",jsapiSignature.getTimestamp());
        map.put("url",jsapiSignature.getUrl());
        return ApiResult.ok(map);
    }


    /**
     * 微信小程序接口能力配置
     */
    @GetMapping("/wxapp/config")
    @ApiOperation(value = "微信小程序接口能力配置",notes = "微信小程序接口能力配置",produces = "text/plain;charset=utf-8")
    public String wxAppConfig(@RequestParam(value = "signature") String signature,
                                                     @RequestParam(value = "timestamp") String timestamp,
                                                     @RequestParam(value = "nonce") String nonce,
                               @RequestParam(name = "echostr", required = false) String echostr) throws WxErrorException {
        WxMaService wxService = WxMaConfiguration.getWxMaService();

        if( wxService.checkSignature(timestamp,nonce,signature)){
            return echostr;
        }
        return "false";
    }

    // 沙盒环境
    private static final String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";
    // 生产环境
    private static final String url_verify = "https://buy.itunes.apple.com/verifyReceipt";

    @AnonymousAccess
    @PostMapping("/ios/notify")
    @ApiOperation(value = "ios回调",notes = "ios回调")
    public ApiResult iosNotify(@RequestBody JSONObject body) {
        log.info("body:{}", JSON.toJSONString(body));
        String orderSn = body.getString("orderSn");
        String receipt = body.getString("receipt");
        if(StringUtils.isBlank(orderSn) || StringUtils.isBlank(receipt)) {
            throw new BusinessException("参数错误");
        }
        //找回充值的订单
        MwUserRecharge userRecharge = userRechargeService.getInfoByOrderId(orderSn);
        if(userRecharge == null) {
            throw new BusinessException("充值订单不存在");
        }
        if(userRecharge.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw new MshopException("该订单已支付");
        }
        //根据不同的环境，选择是去测试环境还是开发环境验证
        //type null 0 生产 其他沙箱
        Integer type = body.getInteger("type");
        String verifyUrl;
        if(type == null || type == 0) {
            verifyUrl = url_verify;
        } else {
            verifyUrl = url_sandbox;
        }
        JSONObject obj = new JSONObject();

        obj.put("receipt-data", receipt);
        obj.put("password", "c3d6eace374c4f038c5d2c8df42b3793");
        JSONObject job = restTemplate.postForObject(verifyUrl, obj, JSONObject.class);

        if (job != null) {
            log.info("job:{}", JSON.toJSONString(job));
            String states = job.getString("status");
//21000 App Store无法读取你提供的JSON数据
//21002 收据数据不符合格式
//21003 收据无法被验证
//21004 你提供的共享密钥和账户的共享密钥不一致
//21005 收据服务器当前不可用
//21006 收据是有效的，但订阅服务已经过期。当收到这个信息时，解码后的收据信息也包含在返回内容中
//21007 收据信息是测试用（sandbox），但却被发送到产品环境中验证
//21008 收据信息是产品环境中使用，但却被发送到测试环境中验证
            if ("21007".equals(states)) {
                log.debug("是沙盒环境，应沙盒测试，否则执行下面");
                // 是沙盒环境，应沙盒测试，否则执行下面
                // 2.再沙盒测试  发送平台验证
                job = restTemplate.postForObject(url_sandbox, obj, JSONObject.class);
                log.debug("3，沙盒环境验证返回的json字符串=" + job.toString());
                states = job.getString("status");
            }
            if ("0".equals(states)) { // 前端所提供的收据是有效的    验证成功
                log.debug("前端所提供的收据是有效的    验证成功");
                JSONObject r_receipt = job.getObject("receipt", JSONObject.class);
                JSONArray in_app = r_receipt.getObject("in_app",JSONArray.class);

                /**
                 * in_app说明：
                 * 验证票据返回的receipt里面的in_app字段，这个字段包含了所有你未完成交易的票据信息。也就是在上面说到的APP完成交易之后，这个票据信息，就会从in_app中消失。
                 * 如果APP不完成交易，这个票据信息就会在in_app中一直保留。(这个情况可能仅限于你的商品类型为消耗型)
                 *
                 * 知道了事件的原委，就很好优化解决了，方案有2个
                 * 1.对票据返回的in_app数据全部进行处理，没有充值的全部进行充值
                 * 2.仅对最新的充值信息进行处理（我们采取的方案）
                 *
                 * 因为采用一方案：
                 * 如果用户仅进行了一次充值，该充值未到账，他不再进行充值了，那么会无法导致。
                 * 如果他通过客服的途径已经进行了补充充值，那么他在下一次充值的时候依旧会把之前的产品票据带回，这时候有可能出现重复充值的情况
                 *
                 */
                if (!in_app.isEmpty()) {
                    String productId = OrderUtil.getIosProductId(userRecharge.getPlatform(), userRecharge.getGrade());
                    Object findProduct = in_app.stream().filter(inAppObj-> productId.equals(((JSONObject)inAppObj).getString("product_id"))).findFirst().orElse(null);

                    //判断product_id，看返回的product_id与实际的充值金额是不是一致，防止骗单
                    if(findProduct == null){
                        throw new MshopException("该订单非法");
                    }
                    JSONObject o = (JSONObject) findProduct;
                    log.info("订单参数: {}", o.toJSONString());

                    //将订单更改为已支付
                    userService.setUserLevel(orderSn);
                    return ApiResult.ok();
                }
            }
        }else{
            //记录错误日志
        }
        return ApiResult.fail("支付失败，请联系客服");

    }

    /**
     * 微信退款回调
     */
    @ApiOperation(value = "退款回调通知处理",notes = "退款回调通知处理")
    @PostMapping("/notify/refund")
    public String parseRefundNotifyResult(@RequestBody String xmlData) {
        try {
            WxPayService wxPayService = WxPayConfiguration.getPayService(PayMethodEnum.WECHAT);
            if(wxPayService == null) {
                wxPayService = WxPayConfiguration.getPayService(PayMethodEnum.WXAPP);
            }
            if(wxPayService == null) {
                wxPayService = WxPayConfiguration.getPayService(PayMethodEnum.APP);
            }
            WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xmlData);
            String orderId = result.getReqInfo().getOutTradeNo();
            BigDecimal refundFee = BigNum.div(result.getReqInfo().getRefundFee(), 100);
            MwStoreOrderQueryVo orderInfo = orderService.getOrderInfo(orderId,null);
            MwStoreOrder storeOrder = new MwStoreOrder();
            //修改状态
            storeOrder.setId(orderInfo.getId());
            orderService.updateById(storeOrder);
            orderService.retrunStock(orderId);
            //售后状态修改
            LambdaQueryWrapper<StoreAfterSales> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StoreAfterSales::getOrderCode, orderId);
            StoreAfterSales storeAfterSales = storeAfterSalesService.getOne(wrapper);
            if (Objects.nonNull(storeAfterSales)) {
                storeAfterSales.setState(AfterSalesStatusEnum.STATUS_3.getValue());
                storeAfterSalesService.updateById(storeAfterSales);
            }
            return WxPayNotifyResponse.success("处理成功!");
        } catch (WxPayException | IllegalAccessException e) {
            log.error(e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }
    /**
     * 微信验证消息
     */
    @GetMapping( value = "/wechat/serve",produces = "text/plain;charset=utf-8")
    @ApiOperation(value = "微信验证消息",notes = "微信验证消息")
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr){

        final WxMpService wxService = WxMpConfiguration.getWxMpService();
        if (wxService == null) {
            throw new IllegalArgumentException("未找到对应配置的服务，请核实！");
        }

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "fail";
    }

    /**
     *微信获取消息
     */
    @PostMapping("/wechat/serve")
    @ApiOperation(value = "微信获取消息",notes = "微信获取消息")
    public void post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        WxMpService wxService = WxMpConfiguration.getWxMpService();

        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if(outMessage == null) {
                return;
            }
            out = outMessage.toXml();
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if(outMessage == null) {
                return;
            }

            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
        }

        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(out);
        writer.close();
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return WxMpConfiguration.getWxMpMessageRouter().route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }

        return null;
    }
    @GetMapping( value = "/wechat/id")
    @ApiOperation(value = "获取微信公众号id",notes = "获取微信公众号id")
    public ApiResult getWechatId(){
        return ApiResult.ok(redisUtils.getY(ShopKeyUtils.getWechatAppId()));

    }




}
