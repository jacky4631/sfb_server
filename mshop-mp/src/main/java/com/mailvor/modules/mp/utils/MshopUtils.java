/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.utils;


import com.mailvor.modules.user.service.dto.WechatUserDto;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName MshopUtils
 * @author wangjun
 * @Date 2020/6/27
 **/
public class MshopUtils {
    public static WechatUserDto getWechtUser(String str) {
        return JSONObject.parseObject(str,WechatUserDto.class);
    }
}
