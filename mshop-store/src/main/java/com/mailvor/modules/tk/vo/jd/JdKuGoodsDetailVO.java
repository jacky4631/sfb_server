/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "商品通用vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdKuGoodsDetailVO {
    /**
     * {
     *   "data" : [ {
     *     "item_info" : "jd",
     *     "brand_code" : "1335829",
     *     "shop_info" : {
     *       "logisticsLvyueScore" : "3.90",
     *       "scoreRankRate" : "46.97",
     *       "shopLevel" : 4,
     *       "userEvaluateScore" : "4.90",
     *       "afterServiceScore" : "4.70",
     *       "shopLabel" : "0",
     *       "shopName" : "玉莲官方旗舰店",
     *       "afsFactorScoreRankGrade" : "高",
     *       "shopId" : 16427134,
     *       "logisticsFactorScoreRankGrade" : "中",
     *       "commentFactorScoreRankGrade" : "高"
     *     },
     *     "itemendprice" : 1.53,
     *     "couponurl" : "",
     *     "commission_info" : {
     *       "isLock" : 0,
     *       "commissionShare" : 2,
     *       "plusCommissionShare" : 2,
     *       "commission" : 0.03,
     *       "startTime" : 1731340800000,
     *       "couponCommission" : 0.03,
     *       "endTime" : 2147529599000
     *     },
     *     "shoptype" : 0,
     *     "goodsname" : "玉莲电池五号七号空调电视遥控器碳性干电池5号7号无线鼠标1.5v儿童玩具AA闹钟鼠标人体秤摆摊玩具电池 7号电池 2粒装",
     *     "couponmoney" : 0,
     *     "couponendtime" : 0,
     *     "sku_tag_list" : [ {
     *       "name" : "便宜包邮",
     *       "index" : 2,
     *       "type" : 1
     *     }, {
     *       "name" : "超18万人在店内买过",
     *       "index" : 8,
     *       "type" : 4
     *     }, {
     *       "name" : "近期销量过万",
     *       "index" : 7,
     *       "type" : 4
     *     }, {
     *       "name" : "7天无理由退货",
     *       "index" : 3,
     *       "type" : 12
     *     }, {
     *       "name" : "超百人正在推广",
     *       "index" : 3,
     *       "type" : 14
     *     }, {
     *       "name" : "超万人正在疯抢",
     *       "index" : 5,
     *       "type" : 4
     *     }, {
     *       "name" : "同款高转化",
     *       "index" : 4,
     *       "type" : 14
     *     } ],
     *     "is_best" : 0,
     *     "itemid" : "Q9Z2Z4vZrarJOMEfbS3fbwRM_3SD7M7iN7rRxqJXSLT",
     *     "jd_image" : "https://img14.360buyimg.com/pop/jfs/t1/122071/1/49677/94371/6708df9bF5b47ae3a/b5dfe828bd1f515f.jpg",
     *     "quota" : 0,
     *     "itemprice" : 1.53,
     *     "commission" : 0.03,
     *     "shop_level" : 4,
     *     "price_info" : {
     *       "lowestPrice" : 1.53,
     *       "lowestCouponPrice" : 1.53,
     *       "price" : 1.53,
     *       "lowestPriceType" : 1
     *     },
     *     "category_info" : {
     *       "cid1Name" : "数码",
     *       "cid2Name" : "数码配件",
     *       "cid2" : 829,
     *       "cid3Name" : "电池/充电器",
     *       "cid3" : 854,
     *       "cid1" : 652
     *     },
     *     "comments" : 20000,
     *     "itemsale" : 10000,
     *     "brand_name" : "玉莲",
     *     "bind_type" : 0,
     *     "white_image" : "",
     *     "commissionshare" : 2,
     *     "couponstarttime" : 0,
     *     "itempic" : "https://img14.360buyimg.com/pop/jfs/t1/122071/1/49677/94371/6708df9bF5b47ae3a/b5dfe828bd1f515f.jpg",
     *     "shopname" : "玉莲官方旗舰店",
     *     "good_comments_share" : 98,
     *     "skuid" : "Q9Z2Z4vZrarJOMEfbS3fbwRM_3SD7M7iN7rRxqJXSLT"
     *   } ]
     * }
     * */
    @JSONField(name = "itemid")
    @Schema(description = "商品id")
    private String goodsId;
    @JSONField(name = "couponurl")
    @Schema(description = "优惠券链接")
    private String couponLink;

    @JSONField(name = "shop_info")
    @Schema(description = "店铺信息")
    private JdKuGoodsShopInfoVO shopInfo;

    @JSONField(name = "commission_info")
    @Schema(description = "佣金信息")
    private JdKuGoodsCommissionInfoVO commissionInfo;

    @JSONField(name = "category_info")
    @Schema(description = "分类信息")
    private JdKuGoodsCateInfoVO cateInfo;

    @JSONField(name = "goodsname")
    @Schema(description = "商品标题")
    private String title;

    @JSONField(name = "itemsale")
    @Schema(description = "销量")
    private String sales;
    @JSONField(name = "comments")
    @Schema(description = "评论数")
    private String comments;

    @JSONField(name = "shopname")
    @Schema(description = "店铺名称")
    private String shopName;

    @JSONField(name = "itempic")
    @Schema(description = "商品预览图")
    private String img;

    @JSONField(name = "itemprice")
    @Schema(description = "原价")
    private Double startPrice;

    @JSONField(name = "itemendprice")
    @Schema(description = "现价")
    private Double endPrice;

    @JSONField(name = "commission")
    @Schema(description = "佣金")
    private Double fee;

    @JSONField(name = "commissionshare")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @JSONField(name = "couponmoney")
    @Schema(description = "优惠券金额")
    private Double coupon;

    @JSONField(name = "couponstarttime")
    @Schema(description = "优惠券开始时间")
    private String couponStart;

    @JSONField(name = "couponendtime")
    @Schema(description = "优惠券结束时间")
    private String couponEnd;

    @JSONField(name = "good_comments_share")
    @Schema(description = "好评率")
    private String goodsCommentShare;

    @JSONField(name = "shoptype")
    @Schema(description = "京东自营 1=是 0=否")
    private String owner;

    @JSONField(name = "jd_image")
    @Schema(description = "商品详情图")
    private String details;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "jd";
}
