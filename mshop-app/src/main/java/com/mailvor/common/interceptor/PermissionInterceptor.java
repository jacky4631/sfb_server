/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.interceptor;


import com.mailvor.api.ApiCode;
import com.mailvor.api.UnAuthenticatedException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.util.JwtToken;
import com.mailvor.constant.ShopConstants;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.RedisUtils;
import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static com.mailvor.utils.StringUtils.getIp;

/**
 * 权限拦截器
 * @author huangyu
 * @date 2020-04-30
 */
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private MwUserService userService;

    @Autowired
    private RedisUtils redisUtils;

    public PermissionInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<AuthCheck> authCheck = this.getAuthCheck(handler);
        if (!authCheck.isPresent()) {
            return true;
        }

        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasLength(bearerToken)) {
            throw new UnAuthenticatedException(ApiCode.UNAUTHORIZED + request.getRequestURL().toString());
        }

        if (!bearerToken.startsWith("Bearer")) {
            throw new UnAuthenticatedException(ApiCode.UNAUTHORIZED);
        }
        String[] tokens = bearerToken.split(" ");
        if (!(tokens.length == 2)) {
            throw new UnAuthenticatedException(ApiCode.UNAUTHORIZED);
        }
        String token = tokens[1];

        Optional<Map<String, Claim>> optionalMap = JwtToken.getClaims(token);
        Map<String, Claim> map = optionalMap
                .orElseThrow(() -> {
                    log.error("请求失败：{} {} 请求ip:{} 请求url:{}",
                            ApiCode.UNAUTHORIZED.getCode(), ApiCode.UNAUTHORIZED.getMessage(), getIp(request), request.getRequestURL());
                    return new UnAuthenticatedException(ApiCode.UNAUTHORIZED);
                });

        String uName = map.get("uName").asString();

        //检测用户是否被踢出
        if (redisUtils.get(ShopConstants.MSHOP_APP_LOGIN_USER + uName + ":" + token) == null) {
            log.error("用户{} token被踢出", uName, token);
            throw new UnAuthenticatedException(ApiCode.UNAUTHORIZED);
        }
        boolean valid = this.hasPermission(authCheck.get(), map);
        if(valid){
            this.setToThreadLocal(map);
        }
        return valid;
    }

    private void setToThreadLocal(Map<String,Claim> map) {
        Integer uid = map.get("uid").asInt();
        Integer scope = map.get("scope").asInt();
        MwUser user = userService.getById(uid);
        if(user == null) {
            throw new UnAuthenticatedException(ApiCode.NOT_PERMISSION);
        }
        LocalUser.set(user, scope);
    }

    private boolean hasPermission(AuthCheck authCheck, Map<String, Claim> map) {
        Integer level = authCheck.value();
        Integer scope = map.get("scope").asInt();
        if (level > scope) {
            throw new UnAuthenticatedException(ApiCode.NOT_PERMISSION);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LocalUser.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private Optional<AuthCheck> getAuthCheck(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AuthCheck authCheck = handlerMethod.getMethod().getAnnotation(AuthCheck.class);
            if (authCheck == null) {
                return Optional.empty();
            }
            return Optional.of(authCheck);
        }
        return Optional.empty();
    }

}
