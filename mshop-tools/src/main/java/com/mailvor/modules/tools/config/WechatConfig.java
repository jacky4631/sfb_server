package com.mailvor.modules.tools.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class WechatConfig implements InitializingBean {
    /**
     * 开放平台移动应用app id
     * */
    @Value("${wx.app.appId}")
    private String appId;
    /**
     * 开放平台移动应用secret
     * */
    @Value("${wx.app.secret}")
    private String appSecret;

    public static String APP_ID;
    public static String APP_SECRET;
    @Override
    public void afterPropertiesSet() {
        APP_ID = appId;
        APP_SECRET = appSecret;
    }
}

