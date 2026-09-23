package com.dtk.api.constant;

/**
 * dtk api: constant class
 *
 * @author 1
 * @date 2020/11/9 16:27
 */
public class DtkApiConstant {

    private DtkApiConstant() {
    }

    public static final String EMPTY_STR = "";

    public static final String ENCRYPT_TYPE_MD5 = "MD5";

    public static final String CLASS_TYPE = "class";

    /**
     * 默认时间格式
     **/
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date默认时区
     **/
    public static final String DATE_TIMEZONE = "GMT+8";

    public static final String SDK_VERSION = "dtk-java-open-platform-sdk-1.0.0";

    public static class Charset {
        public static final String UTF_8 = "UTF-8";
    }

    public static class RequestCommonParam {
        public static final String SIGN = "sign";
        public static final String APP_KEY = "appKey";
        public static final String APP_SECRET = "appSecret";
        public static final String REQUEST_URL = "requestUrl";
    }

    public static class Domain {
        public static final String PROD = "https://openapi.dataoke.com";
    }
}
