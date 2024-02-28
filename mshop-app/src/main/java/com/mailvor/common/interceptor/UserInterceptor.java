/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.interceptor;


import com.mailvor.api.ApiCode;
import com.mailvor.api.UnAuthenticatedException;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.util.JwtToken;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户拦截器 只用于不需要权限 但是可能需要用户的接口
 * @author huangyu
 * @date 2020-04-30
 */
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private MwUserService userService;

    public UserInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<UserCheck> userCheck = this.getUserCheck(handler);
        if (!userCheck.isPresent()) {
            return true;
        }

        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasLength(bearerToken)) {
            return true;
        }

        if (!bearerToken.startsWith("Bearer")) {
            return true;
        }
        String[] tokens = bearerToken.split(" ");
        if (!(tokens.length == 2)) {
            return true;
        }
        String token = tokens[1];

        Optional<Map<String, Claim>> optionalMap = JwtToken.getClaims(token);
        Map<String, Claim> map = optionalMap.orElse(new HashMap<>());

        this.setToThreadLocal(map);
        return true;
    }

    private void setToThreadLocal(Map<String,Claim> map) {
        Claim uidC = map.get("uid");
        Claim scopeC = map.get("scope");
        if(uidC != null && scopeC != null) {
            Integer uid = uidC.asInt();
            Integer scope = scopeC.asInt();
            MwUser user = userService.getById(uid);
            if(user != null) {
                LocalUser.set(user, scope);
            }
        }

    }

    private boolean hasPermission(UserCheck authCheck, Map<String, Claim> map) {
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

    private Optional<UserCheck> getUserCheck(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            UserCheck userCheck = handlerMethod.getMethod().getAnnotation(UserCheck.class);
            if (userCheck == null) {
                return Optional.empty();
            }
            return Optional.of(userCheck);
        }
        return Optional.empty();
    }

}
