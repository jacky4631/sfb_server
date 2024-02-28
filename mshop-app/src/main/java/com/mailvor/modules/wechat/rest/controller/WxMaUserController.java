/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.wechat.rest.param.BindPhoneParam;
import com.mailvor.modules.wechat.rest.param.WxPhoneParam;
import com.mailvor.modules.mp.config.WxMaConfiguration;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.ShopKeyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mazhongjun
 * @date 2020/02/07
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "微信其他", tags = "微信:微信其他")
public class WxMaUserController {

    private final MwUserService userService;
    private final RedisUtils redisUtils;

    @AppLog(value = "公众号绑定手机号", type = 1)
    @AuthCheck
    @PostMapping("/binding")
    @ApiOperation(value = "公众号绑定手机号", notes = "公众号绑定手机号")
    public ApiResult<String> verify(@Validated @RequestBody BindPhoneParam param) {
        Object codeObj = redisUtils.get("code_" + param.getPhone());
        if(codeObj == null){
            return ApiResult.fail("请先获取验证码");
        }
        String code = codeObj.toString();

        if (!StrUtil.equals(code, param.getCaptcha())) {
            return ApiResult.fail("验证码错误");
        }
        MwUser user = LocalUser.getUser();
        if(StrUtil.isNotBlank(user.getPhone())){
            return ApiResult.fail("您的账号已经绑定过手机号码");
        }

        user.setPhone(param.getPhone());
        userService.updateById(user);

        return ApiResult.ok("绑定成功");

    }

    @AppLog(value = "小程序绑定手机号", type = 1)
    @AuthCheck
    @PostMapping("/wxapp/binding")
    @ApiOperation(value = "小程序绑定手机号", notes = "小程序绑定手机号")
    public ApiResult<Map<String,Object>> phone(@Validated @RequestBody WxPhoneParam param) {
        MwUser user = LocalUser.getUser();
        if(StrUtil.isNotBlank(user.getPhone())){
            throw new MshopException("您的账号已经绑定过手机号码");
        }

        //读取redis配置
        String appId = redisUtils.getY(ShopKeyUtils.getWxAppAppId());
        String secret = redisUtils.getY(ShopKeyUtils.getWxAppSecret());
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(secret)) {
            throw new MshopException("请先配置小程序");
        }
        WxMaService wxMaService = WxMaConfiguration.getWxMaService();
        String phone = "";
        try {
            String sessionKey = RedisUtil.get(ShopConstants.MSHOP_MINI_SESSION_KET + user.getUid()).toString();
            WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService()
                    .getPhoneNoInfo(sessionKey, param.getEncryptedData(), param.getIv());
            phone = phoneNoInfo.getPhoneNumber();
            user.setPhone(phone);
            userService.updateById(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MshopException("绑定失败");
        }
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("phone",phone);

        return ApiResult.ok(map,"绑定成功");
    }



}
