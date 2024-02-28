package com.mailvor.modules.pay.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.mailvor.config.PayConfig;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.tools.domain.AlipayConfig;
import com.mailvor.modules.tools.domain.vo.TradeVo;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jane.zhao
 */
@Component
@Slf4j
public class AliPayService {
    @Resource
    private AlipayConfigService alipayConfigService;

    @Resource
    private MwPayChannelService payChannelService;
    @Resource
    private PayService payService;

    @Resource
    private RedisUtils redisUtil;

    @Resource
    private PayConfig payConfig;
    public Map<String, String> alipay(PayChannelDto certProfile, String orderId, String price) throws Exception{

        AlipayConfig alipay = JSON.parseObject(certProfile.getCertProfile(), AlipayConfig.class);
        TradeVo trade = new TradeVo();
        trade.setTotalAmount(price);
        trade.setOutTradeNo(orderId);
        trade.setSubject(payConfig.getTitle());
        trade.setBody(payConfig.getDesc());
        String payUrl = alipayConfigService.toPayAsApp(alipay,trade);

        //调用sdk方法，创建支付，得到支付对象
        Map<String, String> payment = new HashMap<>();
        payment.put("payInfo", payUrl);
        return payment;

    }

    public Map<String, String> alipayWeb(PayChannelDto certProfile, String orderId, String price) throws Exception{

        AlipayConfig alipay = JSON.parseObject(certProfile.getCertProfile(), AlipayConfig.class);
        alipay.setNotifyUrl(certProfile.getNotifyUrl());
        TradeVo trade = new TradeVo();
        trade.setTotalAmount(price);
        trade.setOutTradeNo(orderId);
        trade.setSubject(payConfig.getTitle());
        trade.setBody(payConfig.getDesc());
        String payUrl = alipayConfigService.toPayAsWeb(alipay,trade);

        //调用sdk方法，创建支付，得到支付对象
        Map<String, String> payment = new HashMap<>();
        payment.put("payInfo", payUrl);
        return payment;

    }

    public String notify(HttpServletRequest request) throws IOException, AlipayApiException {
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext ();) {
            String name =  ( String )iter.next();
            String[] values = (String[])requestParams.get(name);
            String valueStr="";
            for(int i = 0;i < values.length; i++){
                valueStr = (i== values.length-1)?valueStr+values[i]:valueStr+values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name,valueStr);
            log.info("支付宝回调参数name:{} value:{}", name, valueStr);
        }
        String orderId = params.get("out_trade_no");
        if(StringUtils.isBlank(orderId)) {
            return "fail";
        }
        //订单状态不是成功 直接返回
        String tradeStatus = params.get("trade_status");
        if(!"TRADE_SUCCESS".equals(tradeStatus)) {
            return "success";
        }
        MwUserRecharge recharge = payService.getRecharge(orderId);
        if(recharge == null) {
            return "fail";
        }
        PayChannelDto payChannel = payService.getChannel(recharge);
        if(payChannel == null) {
            return "fail";
        }
        AlipayConfig alipay = JSON.parseObject(payChannel.getCertProfile(), AlipayConfig.class);
        boolean flag = false;
        if(alipay.getAliPublicCert()!=null){
            flag = AlipaySignature.rsaCertCheckV1(params,
                    alipay.getAliPublicCert(),
                    alipay.getCharset(),alipay.getSignType());
        } else if(alipay.getPublicKey() != null) {
            flag = AlipaySignature.rsaCheckV1(params,
                    alipay.getPublicKey(),
                    alipay.getCharset(),alipay.getSignType());
        }
        if(flag) {
            //减少通道剩余额度
            payChannelService.decPrice(recharge.getPrice(), payChannel.getId());
            //完成订单
            payService.setUserLevel(orderId);
            //清空优惠信息
            String couponKey = OrderUtil.getCouponKey(recharge.getPlatform(), recharge.getUid());
            log.info("删除优惠券key：" + couponKey);
            redisUtil.del(couponKey);
            return "success";
        }
        return "fail";
    }
}
