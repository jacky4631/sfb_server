package com.mailvor.modules.tools.config;

import com.mailvor.utils.ShopKeyUtils;
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

    /**
     * 商户id
     * */
    @Value("${wx.pay.mchId}")
    private String mchId;
    /**
     * 商户密钥
     * */
    @Value("${wx.pay.mchKey}")
    private String mchKey;
    /**
     * 微信证书路径
     * */
    @Value("${wx.pay.mchCertPath}")
    private String mchCertPath;


    public static String APP_ID;
    public static String APP_SECRET;
    public static String MCH_ID;
    public static String MCH_KEY;
    public static String MCH_CERT_PATH;
    @Override
    public void afterPropertiesSet() {
        APP_ID = appId;
        APP_SECRET = appSecret;
        MCH_ID = mchId;
        MCH_KEY = mchKey;
        MCH_CERT_PATH = mchCertPath;
    }
}

