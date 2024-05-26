package com.mailvor.modules.pay.wechat;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.mailvor.api.BusinessException;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.utils.IpUtil;
import com.mailvor.utils.ShopKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mailvor.config.PayConfig.PAY_NAME;

/**
 * @author jane.zhao
 */
@Component
@Slf4j
public class WechatPayService extends PayService {

    public Map<String, Object> pay(PayChannelDto channel, String orderId, String price) throws Exception{
        WxPayAppOrderResult wxPayAppOrderResult = (WxPayAppOrderResult)unifyPay(channel, orderId,price);
        Map<String,Object> jsConfig = new HashMap<>();
        jsConfig.put("partnerid",wxPayAppOrderResult.getPartnerId());
        jsConfig.put("appid",wxPayAppOrderResult.getAppId());
        jsConfig.put("prepayid",wxPayAppOrderResult.getPrepayId());
        jsConfig.put("package",wxPayAppOrderResult.getPackageValue());
        jsConfig.put("noncestr",wxPayAppOrderResult.getNonceStr());
        jsConfig.put("timestamp",wxPayAppOrderResult.getTimeStamp());
        jsConfig.put("sign",wxPayAppOrderResult.getSign());
        return jsConfig;

    }

    /**
     * 统一支付入口
     * @param orderId 单号
     * @return Object
     */
    protected Object unifyPay(PayChannelDto channel, String orderId, String price) {

        WxPayService wxPayService = getWxPayService(channel);

        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setOutTradeNo(orderId);
        Double d = NumberUtil.mul(Double.parseDouble(price),100);
        orderRequest.setTotalFee(d.intValue());
        orderRequest.setSpbillCreateIp(IpUtil.getLocalIP());
        orderRequest.setNotifyUrl(channel.getNotifyUrl());
        orderRequest.setBody(payConfig.getTitle());
        orderRequest.setAttach(payConfig.getDesc());
        orderRequest.setTradeType("APP");
        try {
            return wxPayService.createOrder(orderRequest);
        }catch (WxPayException e) {
            log.info("支付错误信息：{}",e.getMessage());
            throw new BusinessException(e.getMessage());
        }

    }

    /**
     *  获取WxPayService
     * @return
     */
    protected WxPayService getPayService(WechatPayConfig config) {

        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(config.getAppId());

        payConfig.setMchId(config.getMchId());
        payConfig.setMchKey(config.getMchKey());
        payConfig.setApiV3Key(config.getMchKey());
        payConfig.setKeyPath(config.getKeyPath());
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);

        return wxPayService;
    }

    public String notify(String xmlData) throws IOException, AlipayApiException {
        try {
            log.info("微信充值回调信息:{}", xmlData);

            WxPayService wxPayService = getPayService();

            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
            if(isOk(notifyResult)) {
                String orderId = notifyResult.getOutTradeNo();
                MwUserRecharge recharge = userRechargeService.getRecharge(orderId);
                PayChannelDto payChannel = payChannelService.getChannel(recharge.getChannelId());

                finishRecharge(orderId, recharge, payChannel);

                return WxPayNotifyResponse.success("处理成功!");
            }
        } catch (WxPayException e) {
            log.error(e.getMessage());
        }
        return WxPayNotifyResponse.fail("处理失败");
    }

    /**
     * 是否支付成功
     *
     * @param wxPayOrderNotifyResult 微信支付结果
     * @return true成功
     */
    private boolean isOk(WxPayOrderNotifyResult wxPayOrderNotifyResult) {
        return "SUCCESS".equals(wxPayOrderNotifyResult.getResultCode());
    }

    public EntPayResult extract(PayChannelDto channelDto, String openid, String userName, Integer amount) throws WxPayException {
        WxPayService wxPayService = getWxPayService(channelDto);
        EntPayRequest entPayRequest = new EntPayRequest();
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        entPayRequest.setOpenid(openid);
        entPayRequest.setPartnerTradeNo(nonceStr);
        entPayRequest.setCheckName("FORCE_CHECK");
        entPayRequest.setReUserName(userName);
        entPayRequest.setAmount(amount);
        entPayRequest.setDescription("提现成功");
        entPayRequest.setSpbillCreateIp(IpUtil.getLocalIP());
        return wxPayService.getEntPayService().entPay(entPayRequest);

    }

    public WxPayService getWxPayService(PayChannelDto channelDto) {
        WechatPayConfig config = JSON.parseObject(channelDto.getCertProfile(), WechatPayConfig.class);
        return getPayService(config);
    }

    protected WxPayService getPayService(){
        String key = ShopKeyUtils.getMshopWeiXinAppPayService(PAY_NAME);
        Object payServiceObj = redisUtil.get(key);
        if(payServiceObj != null) {
            return (WxPayService) payServiceObj;
        }
        return null;
    }
}
