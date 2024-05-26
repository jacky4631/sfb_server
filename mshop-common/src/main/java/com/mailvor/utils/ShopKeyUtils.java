package com.mailvor.utils;

import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;

/**
 * 处理缓存key值的统一入口，方面后面扩展，
 * 例如:多租户就要在每个key后拼接上租户ID，只要统一修改这里就可以了
 */
public class ShopKeyUtils {
    /**
     *扩展值，默认为空， 把这个值追加到所有key值上
     */
    private static  String getExtendValue(){
        String extendValue= "";
        return  extendValue;
    }

    //*********************************begin mw_system_config 通用值 *****************************************************

    /**
     * api_url
     */
    public static  String getApiUrl(){
        String apiUrl= SystemConfigConstants.API_URL;
        return  apiUrl;
    }
    /**
     * site_url
     */
    public static  String getSiteUrl(){
        String siteUrl= SystemConfigConstants.SITE_URL;
        return  siteUrl;
    }
    /**
     * 腾讯mapkey tengxun_map_key
     */
    public static  String getTengXunMapKey(){
        String tengxunMapKey= SystemConfigConstants.TENGXUN_MAP_KEY;
        return  tengxunMapKey;
    }

    //*********************************begin mw_system_config 业务字段 *****************************************************
    /**
     * store_self_mention
     */
    public static  String getStoreSelfMention(){
        String storeSelfMention= SystemConfigConstants.STORE_SEFL_MENTION;
        return  storeSelfMention+getExtendValue();
    }


    //*********************************begin mw_system_config 微信配置相关字段 *****************************************************

    /**
     * 微信公众号service
     */
    public static  String getMshopWeiXinMpSevice(){
        String mshopWeiXinMpSevice= ShopConstants.MSHOP_WEIXIN_MP_SERVICE;
        return  mshopWeiXinMpSevice+getExtendValue();
    }
    /**
     * 微信公众号service
     */
    public static  String getMshopWeiXinAppSevice(String appName){
        String mshopWeiXinMpSevice= ShopConstants.MSHOP_WEIXIN_APP_SERVICE;
        return  mshopWeiXinMpSevice+ "_" + appName;
    }

    /**
     * 微信公众号id
     */
    public static  String getWechatAppId(){
        String wechatAppId= SystemConfigConstants.WECHAT_APPID;
        return  wechatAppId+getExtendValue();
    }
    /**
     * 微信公众号secret
     */
    public static  String getWechatAppSecret(){
        String wechatAppSecret= SystemConfigConstants.WECHAT_APPSECRET;
        return  wechatAppSecret+getExtendValue();
    }

    /**
     * 微信公众号验证token
     */
    public static  String getWechatToken(){
        String wechatToken= SystemConfigConstants.WECHAT_TOKEN;
        return  wechatToken+getExtendValue();
    }
    /**
     * 微信公众号 EncodingAESKey
     */
    public static  String getWechatEncodingAESKey(){
        String wechatEncodingAESKey= SystemConfigConstants.WECHAT_ENCODINGAESKEY;
        return  wechatEncodingAESKey+getExtendValue();
    }
    /**
     * 微信支付service
     */
    public static  String getMshopWeiXinPayService(String appName){
        String mshopWeiXinPayService= ShopConstants.MSHOP_WEIXIN_PAY_SERVICE;
        return  mshopWeiXinPayService+appName;
    }
    /**
     * 商户号
     */
    public static  String getWxPayMchId(){
        String wxPayMchId= SystemConfigConstants.WXPAY_MCHID;
        return  wxPayMchId+getExtendValue();
    }
    /**
     * 商户秘钥
     */
    public static  String getWxPayMchKey(){
        String wxPayMchKey= SystemConfigConstants.WXPAY_MCHKEY;
        return  wxPayMchKey+getExtendValue();
    }
    /**
     * 商户证书路径
     */
    public static  String getWxPayKeyPath(){
        String wxPayKeyPath= SystemConfigConstants.WXPAY_KEYPATH;
        return  wxPayKeyPath+getExtendValue();
    }
    /**
     * 微信支付app service
     */
    public static  String getMshopWeiXinAppPayService(String appName){
        String mshopWeiXinAppPayService= ShopConstants.MSHOP_WEIXIN_APP_PAY_SERVICE;
        return  mshopWeiXinAppPayService+appName;
    }
    /**
     * 微信开放平台appId
     */
    public static  String getWxAppAppId(){
        String wxAppAppId= SystemConfigConstants.WXAPP_APPID;
        return  wxAppAppId+getExtendValue();
    }
    /**
     * 微信开放平台秘钥
     */
    public static  String getWxAppSecret(){
        String wxAppSecret= SystemConfigConstants.WXAPP_SECRET;
        return  wxAppSecret+getExtendValue();
    }

    /**
     * 微信公众号验证token
     */
    public static  String getWechatMaToken(){
        String wechatToken= SystemConfigConstants.WECHAT_MA_TOKEN;
        return  wechatToken+getExtendValue();
    }
    /**
     * 微信公众号 EncodingAESKey
     */
    public static  String getWechatMaEncodingAESKey(){
        String wechatEncodingAESKey= SystemConfigConstants.WECHAT_MA_ENCODINGAESKEY;
        return  wechatEncodingAESKey+getExtendValue();
    }
    /**
     * 微信小程序service
     * @return
     */
    public static String getMshopWeiXinMaSevice() {
        String mshopWeiXinMaSevice= ShopConstants.MSHOP_WEIXIN_MA_SERVICE;
        return  mshopWeiXinMaSevice+getExtendValue();
    }

}
