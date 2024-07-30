package com.mailvor.modules.tk.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 */
@Getter
@AllArgsConstructor
public enum DataokeApi {
    TB_QUERY_ORDER("https://openapi.dataoke.com/api/tb-service/get-order-details", "v1.0.0","淘系订单查询"),
    JD_QUERY_ORDER("https://openapi.dataoke.com/api/dels/jd/order/get-official-order-list", "v2.0.0","京东订单查询"),
    VIP_QUERY_ORDER("https://openapi.dataoke.com/api/vip/order-list", "v1.0.0","唯品会订单列表"),
    PDD_QUERY_ORDER("https://openapi.dataoke.com/api/dels/pdd/order/incrementSearch", "v1.0.0","拼多多订单查询"),
    DY_QUERY_ORDER("https://openapiv2.dataoke.com/open-api/tiktok/order-list", "v1.0.0","抖客订单"),

    GOODS_LIST("https://openapi.dataoke.com/api/goods/get-goods-list","v1.2.4","商品列表"),
    GOODS_DETAIL("https://openapi.dataoke.com/api/goods/get-goods-details","v1.2.3","单品详情"),
    GOODS_WORD("https://openapi.dataoke.com/api/tb-service/get-privilege-link","v1.3.1","高效转链"),
    GOODS_PARSE("https://openapi.dataoke.com/api/tb-service/parse-content","v1.0.0","淘系万能解析"),
    GOODS_CATEGORY("https://openapi.dataoke.com/api/category/get-super-category","v1.1.0","超级分类"),
    GOODS_PARSE_ALL("https://openapi.dataoke.com/api/dels/kit/contentParse","v1.0.0","剪切板识别"),
    GOODS_SEARCH("https://openapi.dataoke.com/api/goods/get-dtk-search-goods","v2.1.2","大淘客搜索"),
    TB_SEARCH("https://openapi.dataoke.com/api/tb-service/get-tb-service","v2.1.0","联盟搜索"),
    GOODS_SEARCH_HOT("https://openapi.dataoke.com/api/category/get-top100","v1.0.1","热搜记录"),
    GOODS_SEARCH_SUGGESTION("https://openapi.dataoke.com/api/goods/search-suggestion","v1.0.2","联想词"),
    BANNER("https://openapi.dataoke.com/api/goods/topic/carouse-list","v2.0.0","轮播图"),
    TOPIC_LIST("https://openapi.dataoke.com/api/goods/topic/goods-list","v1.0.0","专题商品，轮播图接口返回"),
    TOPIC_LIST2("https://openapi.dataoke.com/api/goods/topic/catalogue","v1.1.0","精选专题"),
    TOPIC_GOODS_LIST2("https://openapi.dataoke.com/api/goods/topic/goods-list","v1.2.2","专题商品"),
    GOODS_NINE("https://openapi.dataoke.com/api/goods/nine/op-goods-list","v3.0.0","9.9包邮精选"),
    GOODS_SIMILAR("https://openapi.dataoke.com/api/goods/list-similer-goods-by-open","v1.2.2","猜你喜欢"),
    GOODS_COMMENT("https://openapi.dataoke.com/api/comment/get-comment-list", "v1.0.0","商品评论"),
    TB_ACTIVITY_LIST("https://openapi.dataoke.com/api/category/get-tb-topic-list", "v1.2.0","官方活动"),
    TB_ACTIVITY_PARSE("https://openapi.dataoke.com/api/tb-service/activity-link", "v1.0.0","官方活动会场转链"),
    TB_DDQ("https://openapi.dataoke.com/api/category/ddq-goods-list", "v1.2.1","咚咚抢"),
    TB_RANK_LIST("https://openapi.dataoke.com/api/goods/get-ranking-list", "v1.3.1","各大榜单"),
    TB_SHOP_CONVERT("https://openapi.dataoke.com/api/dels/shop/convert", "v1.0.0","店铺转链"),
    TB_BRAND_LIST("https://openapi.dataoke.com/api/delanys/brand/get-column-list", "v1.0.0","品牌栏目"),
    TB_BRAND_GOODS_LIST("https://openapi.dataoke.com/api/delanys/brand/get-goods-list", "v1.0.0","单个品牌详情"),

    DY_GOODS_SEARCH("https://openapiv2.dataoke.com/tiktok/tiktok-materials-products-search", "v1.0.0","抖音搜索"),
    DY_GOODS_DETAIL("https://openapiv2.dataoke.com/tiktok/tiktok-materials-products-details", "v1.0.0","抖音详情"),
    DY_WORD("https://openapiv2.dataoke.com/open-api/tiktok-kol-product-share", "v1.0.0","抖音转链"),

    VIP_GOODS_LIST("https://openapi.dataoke.com/api/vip/search-by-keywords", "v1.0.0","唯品会联盟搜索"),
    VIP_GOODS_WORD("https://openapi.dataoke.com/api/vip/promote/link", "v1.0.0","唯品会商品转链"),
    VIP_GOODS_SEARCH("https://openapi.dataoke.com/api/vip/search-by-keywords", "v1.0.0","唯品会联盟搜索"),
    VIP_GOODS_DETAIL("https://openapi.dataoke.com/api/vip/goods-detail", "v1.0.0","唯品会商品详情"),


    JD_GOODS_LIST("https://openapi.dataoke.com/api/dels/jd/goods/search", "v1.0.0","京东联盟搜索"),
    JD_GOODS_DETAIL("https://openapi.dataoke.com/api/dels/jd/goods/get-details", "v1.0.0","京东商品详情"),
    JD_GOODS_WORD("https://openapi.dataoke.com/api/dels/jd/kit/promotion-union-convert","v1.0.0","京东商品转链"),
    JD_PARSE_URL("https://openapi.dataoke.com/api/dels/jd/kit/parseUrl","v1.0.0","京东链接解析"),
    JD_BRAND_LIST("https://openapi.dataoke.com/api/dels/jd/column/list-discount-brand", "v1.0.0","京东大牌折扣"),
    JD_NINES_LIST("https://openapi.dataoke.com/api/dels/jd/column/list-nines", "v1.0.0","京东9.9包邮"),
    JD_RANK_LIST("https://openapi.dataoke.com/api/dels/jd/column/list-real-ranks", "v1.0.0","京东实时榜单"),

    PDD_GOODS_SEARCH("https://openapi.dataoke.com/api/dels/pdd/goods/search", "v2.0.0","拼多多联盟搜索"),
    PDD_GOODS_DETAIL("https://openapi.dataoke.com/api/dels/pdd/goods/detail", "v2.0.0","拼多多"),
    PDD_GOODS_WORD("https://openapi.dataoke.com/api/dels/pdd/kit/goods-prom-generate","v2.0.0","拼多多商品转链"),
    PDD_GOODS_CATE("https://openapi.dataoke.com/api/dels/pdd/category/search", "v1.0.0","拼多多商品类目查询");

    private String url;
    private String version;
    private String desc;

}
