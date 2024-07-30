/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.mailvor.common.service.BaseService;
import com.mailvor.modules.tools.domain.AlipayConfig;
import com.mailvor.modules.tools.domain.vo.TradeVo;

/**
* @author huangyu
* @date 2020-05-13
*/
public interface AlipayConfigService  extends BaseService<AlipayConfig>{

    /**
     * 处理来自PC的交易请求
     * @param alipay 支付宝配置
     * @param trade 交易详情
     * @return String
     * @throws Exception 异常
     */
    String toPayAsPc(AlipayConfig alipay, TradeVo trade) throws Exception;

    /**
     * 处理来自手机网页的交易请求
     * @param alipay 支付宝配置
     * @param trade 交易详情
     * @return String
     * @throws Exception 异常
     */
    String toPayAsWeb(AlipayConfig alipay, TradeVo trade) throws Exception;
    /**
     * 处理来自app的交易请求
     * @param alipay 支付宝配置
     * @param trade 交易详情
     * @return String
     * @throws Exception 异常
     */
    String toPayAsApp(AlipayConfig alipay, TradeVo trade) throws Exception;

    JSONObject faceVerification(AlipayConfig alipay, String certName, String certNo, String phone) throws Exception;
    JSONObject faceVerificationResult(AlipayConfig alipay, String certifyId) throws AlipayApiException;
    /**
     * 支付宝登录
     *
     * @param alipay the alipay
     * @param code   the code
     * @return the alipay user info share response
     * @throws Exception the exception
     */
    AlipayUserInfoShareResponse auth(AlipayConfig alipay, String code) throws Exception;

    /**
     * 支付宝唤起支付宝的拼接字符串
     *
     * @param alipay the alipay
     * @return the string
     * @throws Exception the exception
     */
    String code(AlipayConfig alipay) throws Exception;

    /**
     * 提现
     *
     * @param userId the user id
     * @param amount the amount
     * @return the alipay fund trans uni transfer response
     * @throws AlipayApiException the alipay api exception
     */
    AlipayFundTransUniTransferResponse fund(AlipayConfig config, String userId, String amount) throws AlipayApiException;
    /**
     * 查询配置
     * @return AlipayConfig
     */
    AlipayConfig find();

    /**
     * 更新配置
     * @param alipayConfig 支付宝配置
     * @return AlipayConfig
     */
    void update(AlipayConfig alipayConfig);

    JSONObject cardOCR(AlipayConfig alipay, String ocrType, String name, byte[] fileContent) throws Exception;
}
