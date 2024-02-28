package com.mailvor.modules.pay.adapay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huifu.adapay.Adapay;
import com.huifu.adapay.core.AdapayCore;
import com.huifu.adapay.core.exception.BaseAdaPayException;
import com.huifu.adapay.core.util.AdapaySign;
import com.huifu.adapay.model.*;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.config.PayConfig;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.PayService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import com.mailvor.utils.IpUtil;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author jane.zhao
 */
@Component
@Slf4j
public class AdaPayService{
    @Resource
    private PayService payService;

    @Resource
    private MwPayChannelService payChannelService;

    @Resource
    private MwUserExtractService extractService;

    @Resource
    private PayConfig payConfig;
    public Map<String, Object> alipay(HttpServletRequest request, PayChannelDto certProfile, String orderId, String price) throws Exception{
        String ip = IpUtil.getIpAddress(request);
        AdaPayConfig config = JSON.parseObject(certProfile.getCertProfile(), AdaPayConfig.class);
        //校验商户是否初始化
        if(Adapay.getConfig(config.getMerchantKey()) == null) {
            addMerConfig(config);
        }
        Map<String, Object> paymentParams = new HashMap<>(10);
        paymentParams.put("app_id", config.getAppId());
        paymentParams.put("order_no", orderId);
        paymentParams.put("pay_channel", config.getPayChannel());
        paymentParams.put("pay_amt", String.format("%.2f", Double.valueOf(price)));

        paymentParams.put("goods_title", payConfig.getTitle());
        paymentParams.put("goods_desc", payConfig.getDesc());

        Map<String, Object> deviceInfo = new HashMap<>(2);

        deviceInfo.put("device_ip", ip);

        paymentParams.put("device_info", deviceInfo);
        paymentParams.put("notify_url", certProfile.getNotifyUrl());

        //调用sdk方法，创建支付，得到支付对象
        Map<String, Object> payment = new HashMap<>();
        try {
            String merchantKey = config.getMerchantKey();
            if(StringUtils.isBlank(merchantKey)) {
                payment = Payment.create(paymentParams);
            } else {
                payment = Payment.create(paymentParams, merchantKey);
            }
        } catch (BaseAdaPayException e) {
            e.printStackTrace();
        }

        String error_code = (String) payment.get("error_code");
        if (null != error_code) {
            log.info("创建支付返回参数：" + JSON.toJSONString(payment));

            String error_msg = (String) payment.get("error_msg");
            log.info("error_code:" + error_code + "............." + error_msg);
        }

        return payment;

    }


    public Map<String, Object> bankPay(PayChannelDto certProfile, String orderId, String price,Long uid) {
        AdaPayConfig config = JSON.parseObject(certProfile.getCertProfile(), AdaPayConfig.class);
        //校验商户是否初始化
        if(Adapay.getConfig(config.getMerchantKey()) == null) {
            addMerConfig(config);
        }
        Map<String, Object> paymentParams = new HashMap<>(10);
        paymentParams.put("app_id", config.getAppId());
        paymentParams.put("order_no", orderId);
        paymentParams.put("member_id", uid.toString());
        paymentParams.put("pay_channel", "fast_pay");
        paymentParams.put("pay_amt", String.format("%.2f", Double.valueOf(price)));

        paymentParams.put("goods_title", payConfig.getTitle());
        paymentParams.put("goods_desc", payConfig.getDesc());
        paymentParams.put("pay_mode", "delay");
        paymentParams.put("notify_url", certProfile.getNotifyUrl());
//        paymentParams.put("callback_url", certProfile.getNotifyUrl());

        //调用sdk方法，创建支付，得到支付对象
        Map<String, Object> payment = new HashMap<>();
        try {
            String merchantKey = config.getMerchantKey();
            if(StringUtils.isBlank(merchantKey)) {
                payment = Checkout.create(paymentParams);
            } else {
                payment = Checkout.create(paymentParams, merchantKey);
            }
        } catch (BaseAdaPayException e) {
            e.printStackTrace();
        }

        String error_code = (String) payment.get("error_code");
        if (null != error_code) {
            log.info("创建支付返回参数：" + JSON.toJSONString(payment));

            String error_msg = (String) payment.get("error_msg");
            log.info("error_code:" + error_code + "............." + error_msg);
        }

        return payment;

    }

    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            //验签请参data
            String data = request.getParameter("data");
            //验签请参sign
            String sign = request.getParameter("sign");
            String type = request.getParameter("type");
            log.info("汇付Adapay回调data:{} sign:{} type:{}", data, sign, type);
            if(verifySign(data, sign, type)){
                //验签成功逻辑
                JSONObject dataObj = JSON.parseObject(data);
                String orderId = dataObj.getString("order_no");

                MwUserRecharge recharge = payService.getRecharge(orderId);
                if(recharge != null) {
                    //充值回调
                    PayChannelDto payChannel = payService.getChannel(recharge);
                    //减少通道剩余额度
                    payChannelService.decPrice(recharge.getPrice(), payChannel.getId());
                    //完成订单
                    payService.setUserLevel(orderId);
                } else {
                    //提现回调
                    MwUserExtract extract = extractService.getById(orderId);
                    if(extract != null) {
                        extract.setStatus(1);
                        extractService.saveOrUpdate(extract);
                    }

                }


            }
        }catch (Exception e){
            log.error("异步回调出错，参数，request={} err:{}", request, e);
        }finally{//收到通知,返回success
            response.getOutputStream().write("success".getBytes());
            response.flushBuffer();
        }

    }

    public Map<String, Object> extract(MwUserCardQueryVo card, MwUserExtract userExtract, PayChannelDto certProfile) throws Exception{
        AdaPayConfig config = JSON.parseObject(certProfile.getCertProfile(), AdaPayConfig.class);
        //校验商户是否初始化
        if(Adapay.getConfig(config.getMerchantKey()) == null) {
            addMerConfig(config);
        }

        String memId = userExtract.getUid().toString();
        //创建用户是否
        Map<String, Object> queryMember = executeQueryMember(memId, config.getAppId(), config.getMerchantKey());
        if(!"succeeded".equals(queryMember.get("status"))) {
            Map<String, Object> memberMap = executeCreateMember(memId, config.getAppId(), config.getMerchantKey());
            if(!"succeeded".equals(memberMap.get("status"))) {
                return memberMap;
            }
            Map<String, Object> accountMap = executeCreateSettleAccount(config.getAppId(), card, config.getMerchantKey());
            if(!"succeeded".equals(accountMap.get("status"))) {
                return accountMap;
            }
        }
        //提现
        Map<String, Object> extractMap = executeExtract(userExtract, config.getAppId(), config.getMerchantKey());

        String error_code = (String) extractMap.get("error_code");

        if (null != error_code) {
            log.info("创建支付返回参数：" + JSON.toJSONString(extractMap));

            String error_msg = (String) extractMap.get("error_msg");
            log.info("error_code:" + error_code + "............." + error_msg);
        }
        return extractMap;

    }
    public Map<String, Object> executeQueryMember( String member_id,String app_id, String merchantKey) throws Exception {
        System.out.println("=======execute queryMember begin=======");
        Map<String, Object> memberParams = new  HashMap<>(2);
        memberParams.put("member_id", member_id);
        memberParams.put("app_id", app_id);
        System.out.println("查询用户，请求参数：" + JSON.toJSONString(memberParams));
        Map<String, Object> member = Member.query(memberParams, merchantKey);
        System.out.println("查询用户，返回参数：" + JSON.toJSONString(member));


        System.out.println("=======execute queryMember end=======");

        return member;

    }

    /**
     * 创建 member
     * @return 创建的member 对象
     * @throws Exception 异常
     */
    public Map<String, Object> executeCreateMember(String memId, String appId, String merchantKey) throws Exception {
        System.out.println("=======execute CreateMember begin=======");
        Map<String, Object> memberParams = new  HashMap<String, Object>(2);
        memberParams.put("member_id", memId);
        memberParams.put("app_id",appId);
        System.out.println("创建用户，请求参数：" + JSON.toJSONString(memberParams));
        Map<String, Object> member = Member.create(memberParams, merchantKey);
        System.out.println("创建用户，返回参数：" + JSON.toJSONString(member));
        System.out.println("=======execute CreateMember end=======");

        return member;

    }

    /**
     * 创建 settleCount
     *
     * @return 创建的settleCount 对象
     * @throws Exception 异常
     */
    public Map<String, Object> executeCreateSettleAccount(String appId, MwUserCardQueryVo card, String merchantKey) throws Exception {
        System.out.println("=======execute Create SettleAccount begin=======");
        Map<String, Object> settleCountParams = new HashMap<String, Object>(2);
        Map<String, Object> accountInfo = new HashMap<String, Object>(2);
        accountInfo.put("card_id", card.getBankNo());
        accountInfo.put("card_name", card.getCardName());
        accountInfo.put("cert_id", card.getCardNo());
        accountInfo.put("cert_type", "00");
        accountInfo.put("tel_no", card.getPhone());
        accountInfo.put("bank_acct_type", "2");
        settleCountParams.put("member_id", card.getUid());
        settleCountParams.put("app_id", appId);
        settleCountParams.put("channel", "bank_account");
        settleCountParams.put("account_info", accountInfo);
        System.out.println("创建结算账户，请求参数：" + JSON.toJSONString(settleCountParams));
        Map<String, Object> settleCount = SettleAccount.create(settleCountParams, merchantKey);
        System.out.println("创建结算账户，返回参数：" + JSON.toJSONString(settleCount));
        System.out.println("=======execute Create SettleAccount end=======");

        return settleCount;

    }

    /**
     * 执行提现
     *
     * @return 创建的支付对象
     * @throws Exception 异常
     */
    public Map<String, Object> executeExtract(MwUserExtract userExtract, String appId, String merchantKey) throws BaseAdaPayException {
        System.out.println("=======execute payment begin=======");
        Map<String, Object> confirmResult;
        String memId = userExtract.getUid().toString();
        if("delay".equals(payConfig.getPayAdaMode())) {
            //创建余额提现
            String price = OrderUtil.getRoundFee(userExtract.getExtractPrice()).toString();
            Map<String, Object> balanceParam = new  HashMap(4);
            balanceParam.put("app_id", appId);
            balanceParam.put("adapay_func_code", "settle_accounts.balancePay");
            balanceParam.put("order_no", userExtract.getId() + "_" + System.currentTimeMillis());
            balanceParam.put("out_member_id", "0");
            balanceParam.put("trans_amt", price);
            balanceParam.put("goods_title", "用户提现");
            balanceParam.put("goods_desc", "用户提现");
            balanceParam.put("pay_mode", "delay");

            Map<String, Object> paymentResult = AdapayCommon.requestAdapay(balanceParam, merchantKey);
            log.info("汇付延时分账创建余额提现请求:{} 返回:{}", JSON.toJSONString(balanceParam), JSON.toJSONString(paymentResult));
            if("failed".equals(paymentResult.get("status"))) {
                return paymentResult;
            }

            //创建余额提现确认
            Map<String, Object> confirmParams = new  HashMap(4);
            confirmParams.put("order_no", userExtract.getId()  + "_" + System.currentTimeMillis());
            confirmParams.put("adapay_func_code", "settle_accounts.balanceconfirm");
            confirmParams.put("balance_seq_id", paymentResult.get("balance_seq_id"));
            confirmParams.put("confirm_amt", price);
            List<Map<String, String>> memberList = new ArrayList<>();
            Map<String, String> divMember = new HashMap<>(3);
            divMember.put("member_id", memId);
            divMember.put("amount", price);
            divMember.put("fee_flag", "Y");
            memberList.add(divMember);
            confirmParams.put("div_members", memberList);

            confirmResult = AdapayCommon.requestAdapay(confirmParams, merchantKey);
            log.info("汇付延时分账创建余额提现确认请求:{} 返回:{}", JSON.toJSONString(confirmParams), JSON.toJSONString(confirmResult));

        } else {
            //创建余额提现
            String price = OrderUtil.getRoundFee(userExtract.getExtractPrice()).toString();
            Map<String, Object> balanceParam = new  HashMap(4);
            balanceParam.put("app_id", appId);
            balanceParam.put("adapay_func_code", "settle_accounts.balancePay");
            balanceParam.put("order_no", userExtract.getId() + "_" + System.currentTimeMillis());
            balanceParam.put("out_member_id", "0");
            balanceParam.put("in_member_id", memId);
            balanceParam.put("trans_amt", price);
            balanceParam.put("goods_title", "用户提现");
            balanceParam.put("goods_desc", "用户提现");
            List<Map<String, String>> memberList = new ArrayList<>();
            Map<String, String> divMember = new HashMap<>(3);
            divMember.put("member_id", memId);
            divMember.put("amount", price);
            divMember.put("fee_flag", "Y");
            memberList.add(divMember);
            balanceParam.put("div_members", JSON.toJSONString(memberList));
            confirmResult = AdapayCommon.requestAdapay(balanceParam, merchantKey);
            log.info("汇付实时分账创建余额提现请求:{} 返回:{}", JSON.toJSONString(balanceParam), JSON.toJSONString(confirmResult));

        }

        return confirmResult;
    }

    protected boolean verifySign(String data, String sign, String type) {
        if(StringUtils.isBlank(type) || !"payment.succeeded".equals(type)){
            return false;
        }
        //验签请参publicKey
        String publicKey = AdapayCore.PUBLIC_KEY;
        //验签
        try {
            return AdapaySign.verifySign(data, sign, publicKey);
        } catch (Exception e) {
            log.error("汇付验签错误:{} data:{} sign:{}", e.getMessage(), data, sign);
            return false;
        }
    }


    protected void addMerConfig(AdaPayConfig config) {

            /**
             * debug 模式，开启后与详细的日志
             */
            Adapay.debug = config.getDebug();

            /**
             * prodMode 模式，默认为生产模式，false可以使用mock模式
             */
            Adapay.prodMode = config.getProdMode();

            /**
             * 初始化每个商户配置，服务器启动前，必须通过该方式初始化商户配置完成
             * apiKey为prod模式的API KEY
             * mockApiKey为mock模式的API KEY
             * rsaPrivateKey为商户发起请求时，用于请求参数加签所需要的RSA私钥
             */
            // 创建商户A的商户配置对象
            MerConfig merConfigA = new MerConfig();
            merConfigA.setApiKey(config.getApiKey());
            merConfigA.setApiMockKey(config.getMockApiKey());
            merConfigA.setRSAPrivateKey(config.getRsaPrivateKey());
        try {
            Adapay.addMerConfig(merConfigA, config.getMerchantKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        AdaPayService service = new AdaPayService();
        boolean verifySign = service.verifySign("{\"app_id\":\"app_372a1ed8-6e60-44d1-80f0-b88fdb52769a\",\"created_time\":\"20230423201840\",\"end_time\":\"\",\"id\":\"002212023042320184010496186348028424192\",\"order_no\":\"1650111871262392320\",\"out_trans_id\":\"\",\"party_order_id\":\"\",\"pay_amt\":\"1998.00\",\"pay_channel\":\"alipay\",\"status\":\"failed\",\"error_code\":\"channel_response_code_fail\",\"error_msg\":\"发送失败\"}",
                "G2QcQAGHmndOTfwav0P61JmxnY5B5ib1ST0miIsV4pgS2cWdIXgDoRy37uEaBtjyywEa0BenrAbPbnJk5wif6oxeCZlyP+iZRBOWZcnkXalldK4fDCOTBhHkUFyVZJ2qDyueAaCYp3A1V6m0YybIYcWAaS8PWcQTsXS82Ryh9mQ=",
                "payment.succeeded");
        System.out.println(verifySign);
    }
}
