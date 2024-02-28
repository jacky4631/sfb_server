package com.mailvor.modules.pay.wechat;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.mailvor.api.BusinessException;
import com.mailvor.config.PayConfig;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jane.zhao
 */
@Component
@Slf4j
public class WechatPayService {

    @Resource
    private MwPayChannelService payChannelService;
    @Resource
    private PayService payService;

    private WxPayService wxPayService;

    @Resource
    private PayConfig payConfig;
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

        WechatPayConfig config = JSON.parseObject(channel.getCertProfile(), WechatPayConfig.class);
        WxPayService wxPayService = getPayService(config);

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
        if(wxPayService== null) {
            wxPayService = new WxPayServiceImpl();
        }
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(config.getAppId());

        payConfig.setMchId(config.getMchId());
        payConfig.setMchKey(config.getMchKey());
        payConfig.setKeyPath(config.getKeyPath());
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
        wxPayService.setConfig(payConfig);
        //增加标识
        return wxPayService;
    }

    public String notify(String xmlData) throws IOException, AlipayApiException {
        try {
            log.info("微信充值回调信息:{}", xmlData);

            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
            if(isOk(notifyResult)) {
                String orderId = notifyResult.getOutTradeNo();
                MwUserRecharge recharge = payService.getRecharge(orderId);
                PayChannelDto payChannel = payService.getChannel(recharge);
                payChannelService.decPrice(recharge.getPrice(), payChannel.getId());
                //完成订单
                payService.setUserLevel(orderId);

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
}
