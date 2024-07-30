/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.security.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.modules.security.config.SecurityProperties;
import com.mailvor.modules.security.security.vo.JwtUser;
import com.mailvor.modules.user.vo.OnlineUser;
import com.mailvor.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author huangyu
 * @Date 2019年10月26日21:56:27
 */
@Service
@Slf4j
public class OnlineUserService {
    protected static TimedCache cursorMap = CacheUtil.newTimedCache(2000000);

    private final SecurityProperties properties;
    private RedisUtils redisUtils;

    public OnlineUserService(SecurityProperties properties, RedisUtils redisUtils) {
        this.properties = properties;
        this.redisUtils = redisUtils;
    }

    /**
     * 保存在线用户信息
     * @param jwtUser /
     * @param token /
     * @param request /
     */
    public void save(JwtUser jwtUser, String token, HttpServletRequest request) {
        String job = jwtUser.getDept() + "/" + jwtUser.getJob();
        String ip = StringUtils.getIp(request);
        String browser = StringUtils.getBrowser(request);
        String address = StringUtils.getCityInfo(ip);
        OnlineUser onlineUser = null;
        try {
            onlineUser = new OnlineUser(jwtUser.getUsername(), jwtUser.getNickName(), ip, address, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisUtils.set(properties.getOnlineKey() + token, onlineUser, properties.getTokenValidityInSeconds() / 1000);
    }

    /**
     * 查询全部数据
     * @param filter /
     * @param pageable /
     * @return /
     */
    public Map<String, Object> getAll(String filter, Pageable pageable, int type) {
        if(type != 1) {
            List<OnlineUser> onlineUsers = getAll(filter, type);
            return PageUtil.toPage(
                    PageUtil.toPage(pageable.getPageNumber(), pageable.getPageSize(), onlineUsers),
                    onlineUsers.size());

        }
        Map<String, Object> pageData = getPageData((String)cursorMap.get(pageable.getPageNumber()), pageable.getPageSize(), filter);
        String cursorId = (String)pageData.get("cursorId");
        if(!"0".equals(cursorId)) {
            cursorMap.put(pageable.getPageNumber() + 1, cursorId);
        }
        Object totalObj = cursorMap.get("total");
        if(totalObj == null) {
            List<String> keys = redisUtils.scan(ShopConstants.MSHOP_APP_LOGIN_USER + "*");
            totalObj = keys.size();
            cursorMap.put("total", totalObj);
        }

        return PageUtil.toPage(pageData.get("list"), totalObj);
    }

    /**
     * 查询全部数据，不分页
     * @param filter /
     * @return /
     */
    public List<OnlineUser> getAll(String filter, int type) {
        List<String> keys = null;
        if (type == 1) {
            keys = redisUtils.scan(ShopConstants.MSHOP_APP_LOGIN_USER + "*");
        } else {
            keys = redisUtils.scan(properties.getOnlineKey() + "*");
        }


        Collections.reverse(keys);
        List<OnlineUser> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            OnlineUser onlineUser = (OnlineUser) redisUtils.get(key);
            if (StringUtils.isNotBlank(filter)) {
                if (onlineUser.toString().contains(filter)) {
                    onlineUsers.add(onlineUser);
                }
            } else {
                onlineUsers.add(onlineUser);
            }
        }
        onlineUsers.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUsers;
    }

    public Map<String, Object> getPageData(String cursorId, Integer pageSize, String filter) {

        List<OnlineUser> findUsers = new ArrayList<>(pageSize);
        do{
            Map<String, Object> res =  redisUtils.scan(ShopConstants.MSHOP_APP_LOGIN_USER + "*", pageSize, cursorId);
            cursorId = (String)res.get("cursorId");

            HashSet set = (HashSet)res.get("list");
            List list = new ArrayList<>(set);
            if(StringUtils.isBlank(filter)) {
                findUsers = list;
            } else {
                for(Object obj : list) {
                    OnlineUser onlineUser = (OnlineUser) obj;
                    if(onlineUser.getUserName().contains(filter) || onlineUser.getNickName().contains(filter)) {
                        findUsers.add(onlineUser);
                    }
                }
            }

            //如果返回0 跳出循环
            if("0".equals(cursorId)) {
                break;
            }
        } while (findUsers.size() < pageSize);
        Map<String, Object> res = new HashMap<>(2);
        res.put("cursorId", cursorId);
        res.put("list", findUsers);
        return res;
    }

    /**
     * 踢出用户
     * @param key /
     * @throws Exception /
     */
    public void kickOut(String key) throws Exception {
        key = properties.getOnlineKey() + EncryptUtils.desDecrypt(key);
        redisUtils.del(key);

    }

    /**
     * 踢出移动端用户
     * @param key /
     * @throws Exception /
     */
    public void kickOutT(String key) throws Exception {
        String[] split = StrUtil.split(key, StrUtil.COLON);
        String keyt = ShopConstants.MSHOP_APP_LOGIN_USER + split[0] + StrUtil.COLON + EncryptUtils.desDecrypt(split[1]);
        redisUtils.del(keyt);

    }
    public void kickOutByUsername(String username) {
        String keyt = ShopConstants.MSHOP_APP_LOGIN_USER + username + "*";
        Set<Object> obj = redisUtils.keys(keyt);
        if(obj == null || obj.isEmpty()) {
            return;
        }
        redisUtils.del(obj.toArray(new String[0]));
    }
    /**
     * 退出登录
     * @param token /
     */
    public void logout(String token) {
        String key = properties.getOnlineKey() + token;
        redisUtils.del(key);
    }

    /**
     * 导出
     * @param all /
     * @param response /
     * @throws IOException /
     */
    public void download(List<OnlineUser> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OnlineUser user : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", user.getUserName());
            map.put("用户昵称", user.getNickName());
            map.put("登录IP", user.getIp());
            map.put("登录地点", user.getAddress());
            map.put("登录日期", user.getLoginTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 查询用户
     * @param key /
     * @return /
     */
    public OnlineUser getOne(String key) {
        return (OnlineUser) redisUtils.get(key);
    }

    /**
     * 检测用户是否在之前已经登录，已经登录踢下线
     * @param userName 用户名
     */
    public void checkLoginOnUser(String userName, String igoreToken) {
        List<OnlineUser> onlineUsers = getAll(userName, 0);
        if (onlineUsers == null || onlineUsers.isEmpty()) {
            return;
        }
        for (OnlineUser onlineUser : onlineUsers) {
            if (onlineUser.getUserName().equals(userName)) {
                try {
                    String token = EncryptUtils.desDecrypt(onlineUser.getKey());
                    if (StringUtils.isNotBlank(igoreToken) && !igoreToken.equals(token)) {
                        this.kickOut(onlineUser.getKey());
                    } else if (StringUtils.isBlank(igoreToken)) {
                        this.kickOut(onlineUser.getKey());
                    }
                } catch (Exception e) {
                    log.error("checkUser is error", e);
                }
            }
        }
    }

}
