/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.ApiResult;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.modules.mp.config.WxMaConfiguration;
import com.mailvor.modules.mp.config.WxMpConfiguration;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.MwUserRechargeService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.ShopKeyUtils;
import com.mailvor.utils.StringUtils;
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
import java.util.LinkedHashMap;
import java.util.Map;

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

    private final MwSystemConfigService systemConfigService;

    private final RedisUtils redisUtils;
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
