package com.mailvor.modules.tk.constants;

/**
 * @projectName:dtk-items-openapi
 * @createTime: 2019年11月28日14:13:21
 * @description:
 */
public class TkConstants {

    public static String kuCid;
    //da cms start
    public static final String CMS_PREFIX = "https://cmscg.dataoke.com/cms-v2";
    public static final String API_PREFIX = "https://openapi.dataoke.com/api";
    public static final String CMS_API_PREFIX = "https://dtkapi.ffquan.cn/dtk_go_app_api/v1";

    //爆款验货 分类
    public static final String CHECK_CATE = CMS_PREFIX + "/column-conf?id=606&preview=";

    public static final String RANKING_CATE = CMS_API_PREFIX + "/page-goods-ranking-cate";

    public static final String NINE_CATE = CMS_API_PREFIX + "/page-goods-nine-cate";
    public static final String NINE_TOP = CMS_API_PREFIX + "/page-goods-nine-top";
    public static final String NINE_LIST = CMS_API_PREFIX + "/page-goods-nine?aid=0&cid=%s&pageNo=%s&pageSize=%s&app_key=aqoadw";
    public static final String HOME_DATA_TOP_CATE = "cache:home:top:cate";
    public static final String HOME_DATA_BRAND_LIST = "cache:home:brandList";
    public static final String HOME_DATA_HOT = "cache:home:hot";
    public static final String HOME_DATA_DDQ = "cache:home:ddq";
    public static final String HOME_DATA_BANNER = "cache:home:banner";

    /**
     * 大家都在买
     * */
    public static final String HOME_DATA_EVERY = "cache:home:every";

    public static final String HOME_DATA_CATEGORY_TB = "cache:home:category:tb";
    public static final String HOME_DATA_CATEGORY_PDD = "cache:home:category:pdd";

    public static final String HOME_DATA_TILES = "cache:home:tiles";


    public static final Long HOME_DATA_EXPIRED = 86400L;

}
