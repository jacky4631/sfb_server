package com.mailvor.constant;

import java.util.Arrays;
import java.util.List;

import static com.mailvor.constant.ShopConstants.MSHOP_USER_SHARE;

public class SystemConfigConstants {
    //地址配置
    public final static String API="api";
    public final static String API_URL="api_url";
    public final static String SITE_URL="site_url";
    public final static String UNI_SITE_URL="uni_site_url";
    public final static String TENGXUN_MAP_KEY="tengxun_map_key";
    public final static String FILE_STORE_MODE="file_store_mode";
    //业务相关配置
    public final static String STORE_BROKERAGE_OPEN="store_brokerage_open";
    public final static String STORE_MULTI_VIP_OPEN="store_multi_vip_open";
    public final static String STORE_BROKERAGE_RATIO="store_brokerage_ratio";
    public final static String STORE_BROKERAGE_TWO="store_brokerage_two";
    public final static String STORE_FREE_POSTAGE="store_free_postage";
    public final static String STORE_SEFL_MENTION="store_self_mention";
    public final static String USER_EXTRACT_MIN_PRICE="user_extract_min_price";
    public final static String USER_EXTRACT_MAX_PRICE="user_extract_max_price";

    public final static String USER_EXTRACT_COUNT="user_extract_count";
    /**
     * 自动提现 1开启 2关闭
     * */
    public final static String USER_EXTRACT_AUTO="user_extract_auto";
    /**
     * 自动提现最大金额设置
     * */
    public final static String USER_EXTRACT_MAX="user_extract_max";
    public final static String MSHOP_SHOW_RECHARGE = "mshop_show_recharge";
    //微信相关配置
    public final static String WECHAT_APPID="wechat_appid";
    public final static String WECHAT_APPSECRET="wechat_appsecret";
    public final static String WECHAT_ENCODINGAESKEY="wechat_encodingaeskey";
    public final static String WECHAT_SHARE_IMG="wechat_share_img";
    public final static String WECHAT_SHARE_SYNOPSIS="wechat_share_synopsis";
    public final static String WECHAT_SHARE_TITLE="wechat_share_title";
    public final static String WECHAT_TOKEN="wechat_token";
    public final static String WECHAT_MA_TOKEN="wechat_ma_token";
    public final static String WECHAT_MA_ENCODINGAESKEY="wechat_ma_encodingaeskey";
    public final static String WXAPP_APPID="wxapp_appId";
    public final static String WXAPP_SECRET="wxapp_secret";
    public final static String WXPAY_KEYPATH="wxpay_keyPath";
    public final static String WXPAY_MCHID="wxpay_mchId";
    public final static String WXPAY_MCHKEY="wxpay_mchKey";
    public final static String EXP_APPID = "exp_appId";


    //播放状态变化事件，detail = {code}
    public static final String BINDSTATECHANGE = "bindstatechange";


    //淘宝扣除服务费后比例
    public final static String TK_TB_REBATE_SCALE="tk_tb_rebate_scale";
    //拆红包比例
    public final static String TK_HB_REBATE_SCALE="tk_hb_rebate_scale";
    //拆红包最小倍数
    public final static String TK_HB_MIN_TIMES="tk_hb_min_times";
    //拆红包最大倍数
    public final static String TK_HB_MAX_TIMES="tk_hb_max_times";
    //提现配置
    public final static String EXTRACT_CONFIG="extract_config";
    //支付配置
    public final static String PAY_CONFIG="pay_config";
    //app更新配置
    public final static String UPDATE_CONFIG="update_config";

    /**
     * 登录强制邀请码 1=开启 不存在或者2=关闭
     * */
    public final static String LOGIN_MUST_CODE="login_must_code";


    public final static String HB_UNLOCK_CONFIG="hb_unlock_config";

    public final static String ENERGY_CONFIG="energy_config";
    public final static String MONTH_CARD_CONFIG= "month_card_config";
    public final static String RECOVER_SCALE_CONFIG= "recover_scale_config";


    public final static String SPREAD_HB_COUNT_CONFIG= "spread_hb_count_config";
    public final static String ORDER_CHECK_CONFIG= "order_check_config";

    public static final String HOME_DATA = "url:home";


    public final static String SPREAD_USER_COUNT="spread_user_count";
    public final static String SPREAD_USER_HB="spread_user_hb";

    public final static List<String> INIT_JSON_LIST
            = Arrays.asList(UPDATE_CONFIG, PAY_CONFIG, EXTRACT_CONFIG,
            ENERGY_CONFIG, HB_UNLOCK_CONFIG,MSHOP_USER_SHARE, HOME_DATA);
}
