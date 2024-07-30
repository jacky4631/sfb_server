/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.bean;


import com.mailvor.api.ApiCode;
import com.mailvor.api.UnAuthenticatedException;
import com.mailvor.common.util.JwtToken;
import com.mailvor.common.util.RequestUtils;
import com.mailvor.modules.user.domain.MwUser;
import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 全局user
 * @author mazhongjun
 * @date 2020-04-30
 */
public class LocalUser {
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static void set(MwUser user, Integer scope) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("scope", scope);
        LocalUser.threadLocal.set(map);
    }

    public static void clear() {
        LocalUser.threadLocal.remove();
    }

    public static MwUser getUser() {
        Map<String, Object> map = LocalUser.threadLocal.get();
        if(map != null && map.get("user") != null) {
            return (MwUser)map.get("user");
        }
        return null;
    }

    public static Integer getScope() {
        Map<String, Object> map = LocalUser.threadLocal.get();
        Integer scope = (Integer)map.get("scope");
        return scope;
    }

    public static Long getUidByToken(){
        String bearerToken =  RequestUtils.getRequest().getHeader("Authorization");
        if (StringUtils.isEmpty(bearerToken)) {
            return 0L;
        }

        if (!bearerToken.startsWith("Bearer")) {
            return 0L;
        }
        String[] tokens = bearerToken.split(" ");
        if (!(tokens.length == 2)) {
            return 0L;
        }
        String token = tokens[1];

        Optional<Map<String, Claim>> optionalMap = JwtToken.getClaims(token);
        Map<String, Claim> map = optionalMap
                .orElseThrow(() -> new UnAuthenticatedException(ApiCode.UNAUTHORIZED));

        return  map.get("uid").asLong();
    }
}
