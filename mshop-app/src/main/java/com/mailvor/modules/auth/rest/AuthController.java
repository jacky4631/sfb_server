/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.auth.rest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.enums.SmsTypeEnum;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.common.util.JwtToken;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.auth.param.*;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.pay.alipay.AliPayService;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.services.AuthService;
import com.mailvor.modules.shanyan.ShanyanMobileUtil;
import com.mailvor.modules.shanyan.config.ShanyanConfig;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tools.domain.AlipayConfig;
import com.mailvor.modules.tools.service.AlipayConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserCardService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.mailvor.modules.utils.SmsUtils;
import com.mailvor.utils.RedisUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mailvor.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.modules.user.config.ShopConfig.CONVERT_CONTRACT;

/**
 * @ClassName 认证服务
 * @author huangyu
 * @Date 2020/4/30
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "认证模块", tags = "商城：认证")
public class AuthController {
    private final MwUserService userService;
    private final RedisUtils redisUtil;
    private final AuthService authService;
    private final AlipayConfigService alipayConfigService;

    private final MwSystemConfigService systemConfigService;
    @Value("${single.login}")
    private Boolean singleLogin;


    @Resource
    private ShanyanConfig shanyanConfig;

    @Autowired
    private IGenerator generator;

    @Resource
    private SmsUtils smsUtils;

    @Resource
    private MwUserUnionService userUnionService;

    @Resource
    private MwUserCardService cardService;

    @Resource
    private AliPayService aliPayService;
    /**
     * 根据手机号查询用户状态
     */
    @GetMapping("/wxapp/authPhone/{phone}")
    @ApiOperation(value = "根据手机号查询用户状态", notes = "根据手机号查询用户状态")
    public ApiResult<MwUser> authPhone(@PathVariable String phone) {
        return ApiResult.ok(authService.authPhone(phone)).setMsg("获取成功");
    }

    @ApiOperation("H5登录授权")
    @PostMapping(value = "/login")
    public ApiResult<Map<String, Object>> login(@Validated @RequestBody HLoginParam loginDTO,HttpServletRequest request) {
        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getUsername,loginDTO.getUsername())
                .eq(MwUser::getPassword,SecureUtil.md5(loginDTO.getPassword())),false);

        if(mwUser == null) {
            throw new MshopException("账号或者密码不正确");
        }

        String token =  JwtToken.makeToken(mwUser.getUid(),mwUser.getUsername());
        String expiresTimeStr = JwtToken.getExpireTime(token);

        // 保存在线信息
        authService.save(mwUser, token, request);
        // 返回 token
        Map<String, Object> map = new HashMap<String, Object>(2) {{
            put("token", token);
            put("expires_time", expiresTimeStr);
        }};

        userService.setSpread(loginDTO.getSpread(),mwUser.getUid());

        if(singleLogin){
            //踢掉之前已经登录的token
            authService.checkLoginOnUser(mwUser.getUsername(),token);
        }

        return ApiResult.ok(map).setMsg("登陆成功");
    }

    /**
     * app登录 苏分宝在用
     * */
    @ApiOperation("APP验证码登录")
    @PostMapping(value = "/login/mobile")
    public ApiResult<Map<String, Object>> loginVerify(@Validated @RequestBody LoginVerifyParam loginVerifyParam, HttpServletRequest request) {
        Object codeObj = redisUtil.get("code_" + loginVerifyParam.getAccount());
        if(systemConfigService.getAppLoginWhitelist().contains(loginVerifyParam.getAccount())) {
            if(!"1234".equals(loginVerifyParam.getCaptcha())) {
                throw new MshopException("验证码错误");
            }
        } else {
            if(codeObj == null){
                log.error("手机号{} 获取验证码失败", loginVerifyParam.getAccount());
                throw new MshopException("请先获取验证码");
            }
            String code = codeObj.toString();
            if (!StrUtil.equals(code, loginVerifyParam.getCaptcha())) {
                throw new MshopException("验证码错误");
            }
        }
        return mobileLogin(loginVerifyParam, request);
    }

    @ApiOperation("闪验手机号一键登录")
    @PostMapping(value = "/login/shanyan")
    public ApiResult<Map<String, Object>> shanyanLogin(@Validated @RequestBody LoginShanyanParam param, HttpServletRequest request) {
        String mobile = ShanyanMobileUtil.login(param.getToken(), param.getType(), shanyanConfig);
        if(StringUtils.isBlank(mobile)) {
            throw new MshopException("登录失败");
        }
        LoginVerifyParam verifyParam = new LoginVerifyParam();
        verifyParam.setAccount(mobile);
        return mobileLogin(verifyParam, request);
    }

    protected ApiResult mobileLogin(LoginVerifyParam param, HttpServletRequest request) {
        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getUsername,param.getAccount())
                .or()
                .eq(MwUser::getPhone,param.getAccount()));

        //如果openId不为空 说明通过微信授权登录
        if(StringUtils.isNotBlank(param.getOpenId())) {
            Object wxObj = redisUtil.get("wechat_login_wx_profile:"+ param.getOpenId());
            //当已经存在手机号的用户，绑定微信信息
            if(mwUser != null) {
                updateWechatProfile(wxObj, mwUser.getUid());
            } else {
                Object obj = redisUtil.get("wechat_login:"+ param.getOpenId());
                if(obj != null) {
                    MwUser user = (MwUser) obj;
                    user.setPhone(param.getAccount());
                    user.setStatus(1);
                    userService.save(user);
                    userService.setSpread(param.getSpread(),user.getUid());

                    updateWechatProfile(wxObj, user.getUid());

                    return appLogin(user, request);
                }
            }

        }
        if(mwUser == null) {
            authService.register(generator.convert(param, RegParam.class));
            mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                    .eq(MwUser::getUsername,param.getAccount()));
        }

        userService.setSpread(param.getSpread(),mwUser.getUid());

        return appLogin(mwUser, request);
    }

    protected void updateWechatProfile(Object wxObj, Long uid) {
        if(wxObj != null) {
            //绑定当前平台微信openid到用户
            WechatUserDto wechatUserDto = (WechatUserDto) wxObj;

            MwUserUnion userUnion = userUnionService.getOne(uid);

            if(userUnion != null && userUnion.getWxProfile()!=null) {
                throw new MshopException("该手机号已经绑定微信，请用手机号登录");
            }
            if(userUnion == null) {
                userUnionService.save(uid, wechatUserDto);
            } else {
                userUnionService.update(userUnion, wechatUserDto);
            }
        }
    }

    @AuthCheck
    @ApiOperation("修改密码")
    @PostMapping(value = "/register/reset")
    public ApiResult<Boolean> updatePassword(@Validated @RequestBody UpdatePasswordParam updatePasswordParam, HttpServletRequest request) {
        Object codeObj = redisUtil.get("code_" + updatePasswordParam.getAccount());
        if(codeObj == null){
            throw new MshopException("请先获取验证码");
        }
        String code = codeObj.toString();
        if (!StrUtil.equals(code, updatePasswordParam.getCaptcha())) {
            throw new MshopException("验证码错误");
        }
        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getUsername,updatePasswordParam.getAccount()));

        if(mwUser == null) {
            throw new MshopException("账号不存在,数据错误");
        }
        mwUser.setPassword(SecureUtil.md5(updatePasswordParam.getPassword()));
        boolean b = userService.updateById(mwUser);
        if (!b) {
            throw new MshopException("修改失败");
        }
        String bearerToken = request.getHeader("Authorization");
        String[] tokens = bearerToken.split(" ");
        String token = tokens[1];
        authService.logout(LocalUser.getUser().getUsername(), token);

        return ApiResult.ok(true).setMsg("修改成功");
    }


    @PostMapping("/register")
    @ApiOperation(value = "H5/APP注册新用户", notes = "H5/APP注册新用户")
    public ApiResult<String> register(@Validated @RequestBody RegParam param) {
        Object codeObj = redisUtil.get("code_" + param.getAccount());
        if(codeObj == null){
            return ApiResult.fail("请先获取验证码");
        }
        String code = codeObj.toString();
        if (!StrUtil.equals(code, param.getCaptcha())) {
            return ApiResult.fail("验证码错误");
        }
        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getPhone,param.getAccount()),false);
        if (ObjectUtil.isNotNull(mwUser)) {
            return ApiResult.fail("用户已存在");
        }

        authService.register(param);
        return ApiResult.ok("","注册成功");
    }

    @UserCheck
    @PostMapping("/register/verify")
    @ApiOperation(value = "短信验证码发送", notes = "短信验证码发送")
    public ApiResult<String> mobileVerify(@Validated @RequestBody VerityParam param) {
        return verify(LocalUser.getUser(), param);
    }

    public ApiResult<String> verify(MwUser loginUser, VerityParam param) {
        //校验登录用户修改的手机号和现有手机号是否相同，相同不允许修改
        if(loginUser != null) {
            loginUser = userService.getById(loginUser.getUid());
            if(param.getPhone().equals(loginUser.getPhone())){
                return ApiResult.fail("手机号未变化");
            }
        }
        MwUser mwUser = userService.getOne(Wrappers.<MwUser>lambdaQuery()
                .eq(MwUser::getPhone,param.getPhone()),false);
        if (SmsTypeEnum.REGISTER.getValue().equals(param.getType()) && ObjectUtil.isNotNull(mwUser)) {
            return ApiResult.fail("手机号已注册");
        }
        if (SmsTypeEnum.LOGIN.getValue().equals(param.getType()) && ObjectUtil.isNull(mwUser)) {
            return ApiResult.fail("账号不存在");
        }
        String codeKey = "code_" + param.getPhone();
        if (ObjectUtil.isNotNull(redisUtil.get(codeKey))) {
            return ApiResult.fail("10分钟内有效:" + redisUtil.get(codeKey).toString());
        }
        String code = RandomUtil.randomNumbers(ShopConstants.MSHOP_SMS_SIZE);

        //redis存储
        redisUtil.set(codeKey, code, ShopConstants.MSHOP_SMS_REDIS_TIME);

        String enable = redisUtil.getY("sms_enable");
        if (ShopCommonEnum.ENABLE_2.getValue().toString().equals(enable)) {
            return ApiResult.fail("测试阶段验证码:" + code);
        }

        //发送阿里云短信
        JSONObject json = new JSONObject();
        json.put("code",code);
        try {
            smsUtils.sendSmsNow(param.getPhone(),json.toJSONString());
        } catch (Exception e) {
            redisUtil.del(codeKey);
            e.printStackTrace();
            return ApiResult.ok("发送失败："+e.getMessage());
       }
        return ApiResult.ok("发送成功，请注意查收");


    }

    @AuthCheck
    @ApiOperation(value = "退出登录", notes = "退出登录")
    @PostMapping(value = "/auth/logout")
    public ApiResult<String> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String[] tokens = bearerToken.split(" ");
        String token = tokens[1];
        authService.logout(LocalUser.getUser().getUsername(), token);
        return ApiResult.ok("退出成功");
    }
    /**
     * 微信app授权
     */
    @GetMapping("/wechat/app/auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "微信授权code", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "spread", value = "分销绑定关系的ID", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "微信APP登录", notes = "微信APP登录")
    public ApiResult<Map<String, Object>> appAuthLogin(@RequestParam(value = "code") String code, HttpServletRequest request) {

        WechatLoginParam wechatLoginParam = authService.wechatAppLogin(code);
        if(wechatLoginParam.isRegister()) {
            //如果是注册 直接返回 等待手机号绑定
            Map<String, Object> map = new HashMap<String, Object>(3) {{
                put("openId", wechatLoginParam.getOpenId());
            }};
            return ApiResult.ok(map).setMsg("登陆成功");
        }
        return appLogin(wechatLoginParam.getUser(), request);
    }
    /**
     * 微信app授权
     */
    @PostMapping("/apple/app/auth")
    @ApiOperation(value = "苹果授权", notes = "苹果授权")
    public ApiResult<Map<String, Object>> appleLogin(@RequestBody JSONObject credential,
                                                       HttpServletRequest request) {

        MwUser mwUser = authService.appleLogin(credential);
        return appLogin(mwUser, request);
    }
    /**
     * 支付宝app授权
     */
    @AppLog(value = "支付宝授权", type = 1)
    @GetMapping("/alipay/app/binding")
    @AuthCheck
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spread", value = "分销绑定关系的ID", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "支付宝授权", notes = "支付宝授权")
    public ApiResult alipayAppAuthLogin(@RequestParam(value = "code") String code,
                                                @RequestParam(required = false, value = "spread") String spread,
                                                       HttpServletRequest request) throws Exception {
        Long uid = LocalUser.getUser().getUid();
        MwUser mwUser = userService.getById(uid);
        if(mwUser != null && mwUser.getAliProfile() != null) {
            throw new MshopException("支付宝已授权");
        }
        AlipayConfig alipayConfig = aliPayService.getAlipayConfig();

        AlipayUserInfoShareResponse userInfoShareResponse = alipayConfigService.auth(alipayConfig, code);

        MwUser user = authService.alipayAppLogin(uid, userInfoShareResponse, spread);
        log.info("用户uid{}支付宝授权", uid);
        return ApiResult.ok();


    }
    /**
     * 支付宝app授权
     */
    @GetMapping("/alipay/app/code")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spread", value = "分销绑定关系的ID", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "app授权", notes = "app授权")
    public ApiResult<String> alipayAppAuthCode() throws Exception {
        AlipayConfig alipayConfig = aliPayService.getAlipayConfig();
        String loginStr = alipayConfigService.code(alipayConfig);

        return ApiResult.ok(loginStr);


    }
    /**
     * 微信绑定
     */
    @AuthCheck
    @GetMapping("/wechat/app/binding")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "微信授权code", paramType = "query", dataType = "string"),
    })
    @ApiOperation(value = "微信绑定", notes = "微信绑定")
    public ApiResult<Map<String, Object>> wechatBinding(@RequestParam(value = "code") String code,
                                                       HttpServletRequest request) {
        MwUser mwUser = authService.wechatAppBinding(code,LocalUser.getUser());
        return appLogin(mwUser, request);

    }

    public ApiResult<Map<String, Object>> appLogin(MwUser mwUser,
                                                   HttpServletRequest request) {

        String token =  JwtToken.makeToken(mwUser.getUid(),mwUser.getUsername());
        String expiresTimeStr = JwtToken.getExpireTime(token);
        Integer loginMustCode = Integer.parseInt(systemConfigService.getData(SystemConfigConstants.LOGIN_MUST_CODE));
        //校验是否开启强制邀请码
        String redirect = "0";
        if(loginMustCode != null && loginMustCode == 1) {
            if(mwUser.getSpreadUid() == null || mwUser.getSpreadUid() == 0){
                redirect = "1";
            }
        }
        // 返回 token
        String finalRedirect = redirect;
        Map<String, Object> map = new HashMap<String, Object>(3) {{
            put("token", token);
            put("expires_time", expiresTimeStr);
            put("redirect", finalRedirect);
        }};

        // 保存在线信息
        authService.save(mwUser, token, request);
        if(singleLogin){
            authService.checkLoginOnUser(mwUser.getUsername(),token);
        }
        //是否转换合同
        if(CONVERT_CONTRACT && "dyb".equals(PAY_NAME)) {
            cardService.convertContract(mwUser.getUid());
        }

        return ApiResult.ok(map).setMsg("登陆成功");


    }

    /**
     * 微信公众号授权
     */
    @GetMapping("/wechat/auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "微信授权code", paramType = "query", dataType = "string")
    })
    @ApiOperation(value = "微信公众号登录", notes = "微信公众号登录")
    public ApiResult<Map<String, Object>> authLogin(@RequestParam(value = "code") String code,
                                                    HttpServletRequest request) {

        WechatLoginParam wechatLoginParam = authService.wechatLogin(code);
        if(wechatLoginParam.isRegister()) {
            //如果是注册 直接返回 等待手机号绑定
            Map<String, Object> map = new HashMap<String, Object>(3) {{
                put("openId", wechatLoginParam.getOpenId());
            }};
            return ApiResult.ok(map).setMsg("登陆成功");
        }
        return appLogin(wechatLoginParam.getUser(), request);


    }

}
