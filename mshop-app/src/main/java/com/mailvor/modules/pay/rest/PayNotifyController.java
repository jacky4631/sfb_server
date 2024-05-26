/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.rest;

import com.alipay.api.AlipayApiException;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.modules.pay.adapay.AdaPayService;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.allinpay.syb.SybService;
import com.mailvor.modules.pay.wechat.WechatPayService;
import com.mailvor.modules.pay.yeepay.YeePayService;
import com.mailvor.modules.pay.ysepay.YsePayService;
import com.yinsheng.utils.YsChannelClientException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName WechatController
 * @author huangyu
 * @Date 2019/11/5
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "支付回调", tags = "支付:回调模块")
public class PayNotifyController {
    @Resource
    private SybService sybService;

    @Resource
    private AdaPayService adaPayService;

    @Resource
    private AliPayService aliPayService;

    @Resource
    private WechatPayService wechatPayService;

    @Resource
    private YeePayService yeePayService;

    @Resource
    private YsePayService ysePayService;
    /**
     * 微信支付/充值回调
     */
    @AnonymousAccess
    @PostMapping("/pay/notify/syb")
    @ApiOperation(value = "充值回调",notes = "充值回调")
    @Transactional
    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sybService.notify(request, response);
    }

    @AnonymousAccess
    @PostMapping("/pay/notify/ada")
    @ApiOperation(value = "汇付充值回调",notes = "汇付充值回调")
    @Transactional
    public void adaPayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        adaPayService.notify(request, response);
    }
    @AnonymousAccess
    @PostMapping("/pay/notify/ali")
    @ApiOperation(value = "支付宝充值回调",notes = "支付宝充值回调")
    @Transactional
    public String aliPayNotify(HttpServletRequest request) throws IOException, AlipayApiException {
        return aliPayService.notify(request);
    }

    /**
     * 微信支付/充值回调
     */
    @AnonymousAccess
    @PostMapping("/pay/notify/wechat")
    @ApiOperation(value = "微信APP支付回调",notes = "微信APP支付回调")
    public String wechatNotify(@RequestBody String xmlData) throws AlipayApiException, IOException {
        return wechatPayService.notify(AppFromEnum.APP, xmlData);

    }

    @AnonymousAccess
    @PostMapping("/pay/notify/yee")
    @ApiOperation(value = "易宝充值回调",notes = "易宝充值回调")
    public String yeePayNotify(HttpServletRequest request) throws IOException {
        return yeePayService.notify(request);
    }

    @AnonymousAccess
    @PostMapping("/pay/notify/yse")
    @ApiOperation(value = "银盛充值回调",notes = "银盛充值回调")
    public String ysePayNotify(HttpServletRequest request) throws YsChannelClientException {
        return ysePayService.notify(request);
    }
}
