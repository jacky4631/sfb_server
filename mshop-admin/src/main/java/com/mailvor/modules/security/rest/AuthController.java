/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.security.rest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.api.MshopException;
import com.mailvor.constant.ShopConstants;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.security.config.SecurityProperties;
import com.mailvor.modules.security.security.TokenUtil;
import com.mailvor.modules.security.security.vo.AuthUser;
import com.mailvor.modules.security.security.vo.JwtUser;
import com.mailvor.modules.security.service.OnlineUserService;
import com.mailvor.modules.system.domain.User;
import com.mailvor.modules.system.service.UserService;
import com.mailvor.modules.utils.SmsUtils;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.SecurityUtils;
import com.mailvor.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangyu
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "系统：系统授权接口")
public class AuthController {

    @Value("${loginCode.expiration}")
    private Long expiration;
    @Value("${rsa.private_key}")
    private String privateKey;
    @Value("${single.login}")
    private Boolean singleLogin;
    @Resource
    private SecurityProperties properties;
    @Resource
    private  RedisUtils redisUtils;
    @Resource
    private  UserDetailsService userDetailsService;
    @Resource
    private  OnlineUserService onlineUserService;
    @Resource
    private  TokenUtil tokenUtil;
    @Resource
    private  AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private SmsUtils smsUtils;

    @Resource
    private UserService userService;
    @Log("用户登录")
    @ApiOperation("登录授权")
    @AnonymousAccess
    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUser authUser, HttpServletRequest request){
        // 密码解密
        RSA rsa = new RSA(privateKey, null);
        String password = new String(rsa.decrypt(authUser.getPassword(), KeyType.PrivateKey));
        String code = "123456";
//        // 查询验证码
//        String code = (String) redisUtils.get("admin_code_" + authUser.getUsername());
//        // 清除验证码
//        redisUtils.del(authUser.getUuid());
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("验证码不存在或已过期");
        }
        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = tokenUtil.generateToken(userDetails);
        final JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUser, token, request);
        // 返回 token 与 用户信息
        Map<String,Object> authInfo = new HashMap<String,Object>(2){{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUser);
        }};
        if(singleLogin){
            //踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(),token);
        }
        return ResponseEntity.ok(authInfo);
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo(){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return ResponseEntity.ok(jwtUser);
    }

    @AnonymousAccess
    @ApiOperation("获取验证码")
    @GetMapping(value = "/code")
    public ResponseEntity<Object> getCode(@RequestParam String username){

        User user = userService.findByUsername(username);
        if(user == null) {
            throw new MshopException("用户不存在");
        }
        if(StringUtils.isBlank(user.getPhone())) {
            throw new MshopException("手机号不存在");
        }

        String codeKey = "admin_code_" + username;
        if (ObjectUtil.isNotNull(redisUtils.get(codeKey))) {
            return ResponseEntity.ok("10分钟内有效:" + user.getPhone());
        }
        String code = RandomUtil.randomNumbers(6);

        //redis存储
        redisUtils.set(codeKey, code, ShopConstants.MSHOP_SMS_REDIS_TIME);

        //发送阿里云短信
        JSONObject json = new JSONObject();
        json.put("code",code);
        try {
            smsUtils.sendSmsNow(user.getPhone(),json.toJSONString());
        } catch (Exception e) {
            redisUtils.del(codeKey);
            e.printStackTrace();
            return ResponseEntity.ok("发送失败："+e.getMessage());
        }
        return ResponseEntity.ok("发送成功至" + user.getPhone());
    }

    @ApiOperation("退出登录")
    @AnonymousAccess
    @DeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request){
        onlineUserService.logout(tokenUtil.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
