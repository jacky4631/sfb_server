package com.mailvor.modules.tk.constants;

/**
 * @projectName:dtk-items-openapi
 * @createTime: 2019年11月28日14:13:21
 * @description:
 */
public class TkConstants {

    public static final String KU_CID = "FazG6352";
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
    public static final String TOP_CATE = "cache:index:top:cate";
    public static final String MENU_DATA = "cache:index:menu";
    public static final String GOODS_LIST_DATA = "cache:index:goodsList";
    public static final String BRAND_LIST_DATA = "cache:index:brandList";
    public static final String HOT_DATA = "cache:index:hot";
    public static final String DDQ_DATA = "cache:index:ddq";
    public static final String HOME_BANNER = "cache:home:banner";


    public static final String HOME_CATEGORY = "cache:home:category";
    public static final String HOME_TILES = "cache:home:tiles";

}
