/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.constant;

/**
 * 商城统一常量
 * @author huangyu
 * @since 2020-02-27
 */
public interface ShopConstants {

	/**
	 * 订单自动取消时间（分钟）
	 */
	long ORDER_OUTTIME_UNPAY = 30;
	/**
	 * 订单自动收货时间（天）
	 */
	long ORDER_OUTTIME_UNCONFIRM = 7;
	/**
	 * redis订单未付款key
	 */
	String REDIS_ORDER_OUTTIME_UNPAY = "order:unpay:";
	/**
	 * redis订单收货key
	 */
	String REDIS_ORDER_OUTTIME_UNCONFIRM = "order:unconfirm:";

	/**
	 * redis拼团key
	 */
	String REDIS_PINK_CANCEL_KEY = "pink:cancel:";

	/**
	 * 微信支付service
	 */
	String MSHOP_WEIXIN_PAY_SERVICE = "mshop_weixin_pay_service";

	/**
	 * 微信支付小程序service
	 */
	String MSHOP_WEIXIN_MINI_PAY_SERVICE = "mshop_weixin_mini_pay_service";

	/**
	 * 微信支付app service
	 */
	String MSHOP_WEIXIN_APP_PAY_SERVICE = "mshop_weixin_app_pay_service";

	/**
	 * 微信公众号service
	 */
	String MSHOP_WEIXIN_MP_SERVICE = "mshop_weixin_mp_service";
	/**
	 * 微信APPservice
	 */
	String MSHOP_WEIXIN_APP_SERVICE = "mshop_weixin_app_service";
	/**
	 * 微信小程序service
	 */
	String MSHOP_WEIXIN_MA_SERVICE = "mshop_weixin_ma_service";

	/**
	 * 商城默认密码
	 */
	String MSHOP_DEFAULT_PWD = "123456";

	/**
	 * 商城默认注册图片
	 */
	String MSHOP_DEFAULT_AVATAR = "https://oss.mailvor.cn/sfb/default_logo2.jpg";

	/**
	 * 腾讯地图地址解析
	 */
	String QQ_MAP_URL = "https://apis.map.qq.com/ws/geocoder/v1/";

	/**
	 * redis首页键
	 */
	String MSHOP_REDIS_INDEX_KEY = "mshop:index_data";

	/**
	 * 配置列表缓存
	 */
	String MSHOP_REDIS_CONFIG_DATAS = "mshop:config_datas";

	/**
	 * 充值方案
	 */
	String MSHOP_RECHARGE_PRICE_WAYS = "mshop_recharge_price_ways";
	/**
	 * 首页banner
	 */
	String MSHOP_HOME_BANNER = "mshop_home_banner";
	/**
	 * 首页菜单
	 */
	String MSHOP_HOME_MENUS = "mshop_home_menus";
	/**
	 * 首页滚动新闻
	 */
	String MSHOP_HOME_ROLL_NEWS = "mshop_home_roll_news";
	/**
	 * 热门搜索
	 */
	String MSHOP_HOT_SEARCH = "mshop_hot_search";
	/**
	 * 个人中心菜单
	 */
	String MSHOP_MY_MENUES = "mshop_my_menus";
	/**
	 * 秒杀时间段
	 */
	String MSHOP_SECKILL_TIME = "mshop_seckill_time";
	/**
	 * 签到天数
	 */
	String MSHOP_SIGN_DAY_NUM = "mshop_sign_day_num";

	/**
	 * 打印机配置
	 */
	String MSHOP_ORDER_PRINT_COUNT = "order_print_count";
	/**
	 * 飞蛾用户信息
	 */
	String MSHOP_FEI_E_USER = "fei_e_user";
	/**
	 * 飞蛾用户密钥
	 */
	String MSHOP_FEI_E_UKEY = "fei_e_ukey";

	/**
	 * 打印机配置
	 */
	String MSHOP_ORDER_PRINT_COUNT_DETAIL = "order_print_count_detail";

	/**
	 * 短信验证码长度
	 */
	int MSHOP_SMS_SIZE = 4;

	/**
	 * 短信缓存时间
	 */
	long MSHOP_SMS_REDIS_TIME = 60L;

	//零标识
	String MSHOP_ZERO =  "0";

	//业务标识标识
	String MSHOP_ONE =  "1";

	//目前完成任务数量是3
	int TASK_FINISH_COUNT = 3;

	int MSHOP_ONE_NUM = 1;

	String MSHOP_ORDER_CACHE_KEY = "mshop:order";

	long MSHOP_ORDER_CACHE_TIME = 600L;

	String WECHAT_MENUS =  "wechat_menus";

	String MSHOP_EXPRESS_SERVICE = "mshop_express_service";

	String MSHOP_REDIS_SYS_CITY_KEY = "mshop:city_list";

	String MSHOP_REDIS_CITY_KEY = "mshop:city";

	String MSHOP_APP_LOGIN_USER = "app-online-token:";

	String MSHOP_WECHAT_PUSH_REMARK = "mshop为您服务！";

	String DEFAULT_UNI_H5_URL = "https://h5.mailvor.com";

	String MSHOP_MINI_SESSION_KET = "mshop:session_key:";

	String MSHOP_HOT_WORDS = "mshop:hot_words";

	String MSHOP_USER_HISTORY = "mshop:history:";


	String MSHOP_USER_SHARE = "mshop:share:images";

	String PID_TB_BINDING = "pid:tb:binding:";

	String PID_TB_POOL = "pid:tb:pool";

	String FEE_TODAY_UID = "fee:today:uid";

	/**公众号二维码*/
	String WECHAT_FOLLOW_IMG="wechat_follow_img";
	/**后台api地址*/
	String ADMIN_API_URL="admin_api_url";
}
