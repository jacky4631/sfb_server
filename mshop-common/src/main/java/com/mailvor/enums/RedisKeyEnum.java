/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rediskey 相关枚举
 */
@Getter
@AllArgsConstructor
public enum  RedisKeyEnum {

    WXAPP_APPID("wxapp_appId","微信小程序id"),
    WXAPP_SECRET("wxapp_secret","微信小程序秘钥"),
    WECHAT_APPID("wechat_appid","微信公众号id"),
    WECHAT_APPSECRET("wechat_appsecret","微信公众号secret"),
    WECHAT_TOKEN("wechat_token","微信公众号验证token"),
    WECHAT_ENCODINGAESKEY("wechat_encodingaeskey","EncodingAESKey"),
    TENGXUN_MAP_KEY("tengxun_map_key","腾讯mapkey");

    private String value;
    private String desc;
}
