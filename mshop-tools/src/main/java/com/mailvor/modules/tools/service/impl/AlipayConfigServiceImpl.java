/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.Participant;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.tools.config.AliOcrConfig;
import com.mailvor.config.PayConfig;
import com.mailvor.modules.tools.domain.AlipayConfig;
import com.mailvor.modules.tools.domain.vo.TradeVo;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.tools.service.mapper.AlipayConfigMapper;
import com.mailvor.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;


/**
* @author huangyu
* @date 2020-05-13
*/
@Slf4j
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "alipayConfig")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AlipayConfigServiceImpl extends BaseServiceImpl<AlipayConfigMapper, AlipayConfig> implements AlipayConfigService {

    private static AlipayClient alipayClient = null;
    @Override
    public String toPayAsPc(AlipayConfig alipay, TradeVo trade) throws Exception {

        if(alipay.getId() == null){
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        AlipayClient alipayClient = new DefaultAlipayClient(alipay.getGatewayUrl(), alipay.getAppId(), alipay.getPrivateKey(), alipay.getFormat(), alipay.getCharset(), alipay.getPublicKey(), alipay.getSignType());

        // 创建API对应的request(电脑网页版)
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

        // 订单完成后返回的页面和异步通知地址
        request.setReturnUrl(alipay.getReturnUrl());
        request.setNotifyUrl(alipay.getNotifyUrl());
        // 填充订单参数
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+trade.getOutTradeNo()+"\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":"+trade.getTotalAmount()+"," +
                "    \"subject\":\""+trade.getSubject()+"\"," +
                "    \"body\":\""+trade.getBody()+"\"," +
                "    \"extend_params\":{" +
                "    \"sys_service_provider_id\":\""+alipay.getSysServiceProviderId()+"\"" +
                "    }"+
                "  }");//填充业务参数
        // 调用SDK生成表单, 通过GET方式，口可以获取url
        return alipayClient.pageExecute(request, "GET").getBody();

    }

    @Override
    public String toPayAsWeb(AlipayConfig alipay, TradeVo trade) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(alipay.getGatewayUrl(), alipay.getAppId(), alipay.getPrivateKey(), alipay.getFormat(), alipay.getCharset(), alipay.getPublicKey(), alipay.getSignType());

        double money = Double.parseDouble(trade.getTotalAmount());
        double maxMoney = 5000;
        if(money <= 0 || money >= maxMoney){
            throw new BadRequestException("测试金额过大");
        }
        // 创建API对应的request(手机网页版)
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setReturnUrl(alipay.getReturnUrl());
        request.setNotifyUrl(alipay.getNotifyUrl());
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+trade.getOutTradeNo()+"\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":"+trade.getTotalAmount()+"," +
                "    \"subject\":\""+trade.getSubject()+"\"," +
                "    \"body\":\""+trade.getBody()+"\"," +
                "    \"extend_params\":{" +
                "    \"sys_service_provider_id\":\""+alipay.getSysServiceProviderId()+"\"" +
                "    }"+
                "  }");
        return alipayClient.pageExecute(request, "GET").getBody();
    }
    @Override
    public String toPayAsApp(AlipayConfig alipay, TradeVo trade) throws Exception {
        //构造client
        AlipayClient alipayClient = initAlipayClient(alipay);

        double money = Double.parseDouble(trade.getTotalAmount());
        double maxMoney = 5000;
        if(money <= 0 || money >= maxMoney){
            throw new BadRequestException("测试金额过大");
        }
        // 创建API对应的request(app版)
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(trade.getBody());
        model.setSubject(trade.getSubject());
        model.setOutTradeNo(trade.getOutTradeNo());
        model.setTotalAmount(trade.getTotalAmount());
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(alipay.getNotifyUrl());
        //这里和普通的接口调用不同，使用的是sdkExecute
        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        return response.getBody();//就是orderString 可以直接给客户端请求，无需再做处理。
    }
    @Override
    public AlipayUserInfoShareResponse auth(AlipayConfig alipay, String code) throws Exception {
        AlipayClient alipayClient = initAlipayClient(alipay);
        AlipaySystemOauthTokenRequest request =  new  AlipaySystemOauthTokenRequest(); //创建API对应的request类
        request.setGrantType("authorization_code");
        request.setCode(code);
        AlipaySystemOauthTokenResponse response = alipayClient.certificateExecute(request); //通过alipayClient调用API，获得对应的response类
        log.info("Alipay auth: {}", response.getBody());
        AlipayUserInfoShareRequest userInfoShareRequest =  new  AlipayUserInfoShareRequest();
        AlipayUserInfoShareResponse userInfoShareResponse = alipayClient.certificateExecute(userInfoShareRequest,response.getAccessToken());
        log.info("Alipay auth user: {}", userInfoShareResponse.getBody());
        return  userInfoShareResponse;
    }


    @Override
    public String code(AlipayConfig alipay) throws Exception {
        //目前未做sign签名，支付宝未强制校验
        String code = "apiname=com.alipay.account.auth" +
                "&method=alipay.open.auth.sdk.code.get" +
                "&app_id=" + alipay.getAppId() +
                "&app_name=mc" +
                "&biz_type=openservice" +
                "&pid=" +alipay.getSysServiceProviderId()+
                "&product_id=APP_FAST_LOGIN" +
                "&scope=kuaijie" +
                "&target_id=" + System.currentTimeMillis()+
                "&auth_type=AUTHACCOUNT" +
                "&sign_type=RSA2" +
                "&sign=m6K7Dz4CxPAgLn2uwIjGSmgRcOBYtHcqaYqLc85/C6PCqoIu6tUHDmx5/hb0xy+dMCdQoFcQWKRGzBl040g/6avD/PhOUSUi9Cmtd2HxSzEEjk7LuFn9QrpAmcM7/tub+K/G/2rQp9ce8FY2RCbJ/sFDA09M5B+2gqzy9Qkc5fE=";

        return code;
    }

    @Override
    public AlipayFundTransUniTransferResponse fund(AlipayConfig config, String userId, String amount) throws AlipayApiException {

        AlipayClient alipayClient = initAlipayClient(config);
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
        model.setOutBizNo(System.currentTimeMillis()+"");
        model.setBizScene("DIRECT_TRANSFER");
        model.setRemark("提现成功");
        Participant payeeInfo = new Participant();
        payeeInfo.setIdentity(userId);
        payeeInfo.setIdentityType("ALIPAY_USER_ID");
        model.setPayeeInfo(payeeInfo);
        model.setTransAmount(amount);
        model.setProductCode("TRANS_ACCOUNT_NO_PWD");
        model.setOrderTitle(PayConfig.appName() + "提现");
        request.setBizModel(model);
        AlipayFundTransUniTransferResponse response = alipayClient.certificateExecute(request);
        System.out.println(JSON.toJSONString(response));
        if (response.isSuccess()) {
            System.out.println("提现成功");
        } else {
            System.out.println("提现失败");
        }
        return response;
    }

    @Override
//    @Cacheable(key = "'1'")
    public AlipayConfig find() {
        String name = PayConfig.PAY_NAME;
        if(StringUtils.isBlank(name)) {
            return findDefault();
        }
        AlipayConfig alipayConfig = getOne(new LambdaQueryWrapper<AlipayConfig>()
                .eq(AlipayConfig::getName,name));
        if(alipayConfig == null) {
            return findDefault();
        }
        return alipayConfig;
    }

    protected AlipayConfig findDefault() {
        AlipayConfig alipayConfig = this.list().get(0);
        return alipayConfig;
    }

    @Override
//    @CachePut(key = "'1'")
    @Transactional(rollbackFor = Exception.class)
    public void update(AlipayConfig alipayConfig) {
         this.save(alipayConfig);
    }

    public CertAlipayRequest initAlipayRequest(AlipayConfig alipay) {
        //        AlipayClient alipayClient = new DefaultAlipayClient(alipay.getGatewayUrl(), alipay.getAppId(), alipay.getPrivateKey(),
//        alipay.getFormat(), alipay.getCharset(), alipay.getPublicKey(), alipay.getSignType());
//构造client
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest ();
        //设置网关地址
        certAlipayRequest.setServerUrl(alipay.getGatewayUrl());
        //设置应用Id
        certAlipayRequest.setAppId(alipay.getAppId());
        //设置应用私钥
        certAlipayRequest.setPrivateKey(alipay.getPrivateKey());
        //设置请求格式，固定值json
        certAlipayRequest.setFormat(alipay.getFormat());
        //设置字符集
        certAlipayRequest.setCharset(alipay.getCharset());
        //设置签名类型
        certAlipayRequest.setSignType(alipay.getSignType());
        //设置应用公钥证书路径
        certAlipayRequest.setCertPath(alipay.getAppPublicCert());
        //设置支付宝公钥证书路径
        certAlipayRequest.setAlipayPublicCertPath(alipay.getAliPublicCert());
        //设置支付宝根证书路径
        certAlipayRequest.setRootCertPath(alipay.getAliRootCert());
        //设置应用公钥证书路径
//        certAlipayRequest.setCertPath("F:\\sufenbaoKey\\appCertPublicKey_2021003147661140.crt");
//        //设置支付宝公钥证书路径
//        certAlipayRequest.setAlipayPublicCertPath("F:\\sufenbaoKey\\alipayCertPublicKey_RSA2.crt");
//        //设置支付宝根证书路径
//        certAlipayRequest.setRootCertPath("F:\\sufenbaoKey\\alipayRootCert.crt");
        return certAlipayRequest;
    }
    public AlipayClient initAlipayClient(AlipayConfig alipay) throws AlipayApiException {
        if(alipay.getAppId() == null){
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        if(alipayClient == null) {
            CertAlipayRequest certAlipayRequest = initAlipayRequest(alipay);
            //构造client
            alipayClient = new DefaultAlipayClient(certAlipayRequest);
        }
        return alipayClient;
    }

    @Override
    public JSONObject faceVerification(AlipayConfig alipay, String certName, String certNo, String phone) throws Exception {
        //构造client
        AlipayClient alipayClient = initFaceAlipayClient(alipay);

        DatadigitalFincloudGeneralsaasFaceVerificationInitializeRequest request = new DatadigitalFincloudGeneralsaasFaceVerificationInitializeRequest();
        request.setNeedEncrypt(true);
        request.setBizContent("{" +
                "  \"outer_order_no\":\"" + phone + System.currentTimeMillis() + "\"," +
                "  \"biz_code\":\"DATA_DIGITAL_BIZ_CODE_FACE_VERIFICATION\"," +
                "  \"identity_type\":\"CERT_INFO\"," +
                "  \"cert_type\":\"IDENTITY_CARD\"," +
                "  \"cert_name\":\""+ certName+"\"," +
                "  \"cert_no\":\""+ certNo+ "\"," +
                "  \"phone_no\":\""+ phone + "\"" +
                "}");
        DatadigitalFincloudGeneralsaasFaceVerificationInitializeResponse response = alipayClient.certificateExecute(request);
        log.info("人脸认证：姓名{} 身份证号码{} 手机号{} 生成支付宝流水{}", certName, certNo, phone, JSON.toJSONString(response));
        //todo 需要做验签
        return JSON.parseObject(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
    }

    @Override
    public JSONObject faceVerificationResult(AlipayConfig alipay, String certifyId) throws AlipayApiException {
        //构造client
        AlipayClient alipayClient = initAlipayClient(alipay);

        DatadigitalFincloudGeneralsaasFaceVerificationQueryRequest  request = new DatadigitalFincloudGeneralsaasFaceVerificationQueryRequest ();
//        request.setNeedEncrypt(true);
        request.setBizContent("{" +
                "  \"certify_id\":\"" + certifyId + "\"," +
                "  \"need_alive_photo\":\"Y_O\"" +
                "}");
        DatadigitalFincloudGeneralsaasFaceVerificationQueryResponse  response = alipayClient.certificateExecute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        //todo 需要做验签
        return JSON.parseObject(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
    }

    public AlipayClient initFaceAlipayClient(AlipayConfig alipay) throws AlipayApiException {
        if(alipay.getId() == null){
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        CertAlipayRequest certAlipayRequest = initAlipayRequest(alipay);

        certAlipayRequest.setEncryptor(AliOcrConfig.ENCRYPT_KEY);
        certAlipayRequest.setEncryptType("AES");
        //构造client
        AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);

        return alipayClient;
    }

    @Override
    public JSONObject cardOCR(AlipayConfig alipay, String ocrType, String name, byte[] fileContent) throws Exception {
        AlipayClient alipayClient = initAlipayClient(alipay);
        DatadigitalFincloudGeneralsaasOcrServerDetectRequest request = new DatadigitalFincloudGeneralsaasOcrServerDetectRequest();
        request.setOcrType(ocrType);
        request.setOuterOrderNo(System.currentTimeMillis()+"");
        FileItem FileContent = new FileItem(name, fileContent);
        request.setFileContent(FileContent);
        DatadigitalFincloudGeneralsaasOcrServerDetectResponse response = alipayClient.certificateExecute(request);
        return JSON.parseObject(response.getBody());
    }
}
