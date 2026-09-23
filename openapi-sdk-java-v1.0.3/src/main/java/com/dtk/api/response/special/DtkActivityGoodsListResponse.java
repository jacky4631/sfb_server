package com.dtk.api.response.special;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 活动商品响应结果实体
 *
 * @author 1
 * @date 2020/11/12 16:19
 */
@Getter
@Setter
public class DtkActivityGoodsListResponse {
    @ApiModelProperty(value = "定金，若无定金，则显示0")
    private BigDecimal quanMLink;
    @ApiModelProperty(value = "立减，若无立减金额，则显示0")
    private BigDecimal hzQuanOver;
    @ApiModelProperty(value = "0.不包运费险 1.包运费险")
    private Integer yunfeixian;
    @ApiModelProperty(value = "预估淘礼金")
    private BigDecimal estimateAmount;
    @ApiModelProperty(value = "店铺logo")
    private String shopLogo;
    @ApiModelProperty(value = "偏远地区包邮，1-包邮，0-否")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty(value = "商品id")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;
    @ApiModelProperty(value = "商品淘宝链接")
    private String itemLink;
    @ApiModelProperty(value = "淘宝标题")
    private String title;
    @ApiModelProperty(value = "大淘客短标题")
    private String dtitle;
    @ApiModelProperty(value = "推广文案")
    private String desc;
    @ApiModelProperty(value = "商品在大淘客的分类id")
    private Integer cid;
    @ApiModelProperty(value = "商品在大淘客的二级分类id，该分类为前端分类，一个商品可能有多个二级分类id")
    private List<Integer> subcid;
    @ApiModelProperty(value = "商品在淘宝的分类id")
    private Integer tbcid;
    @ApiModelProperty(value = "商品主图链接")
    private String mainPic;
    @ApiModelProperty(value = "营销主图链接")
    private String marketingMainPic;
    @ApiModelProperty(value = "商品原价")
    private BigDecimal originalPrice;
    @ApiModelProperty(value = "券后价")
    private BigDecimal actualPrice;
    @ApiModelProperty(value = "折扣力度")
    private BigDecimal discounts;
    @ApiModelProperty(value = "佣金类型，0-通用，1-定向，2-高佣，3-营销计划")
    private Integer commissionType;
    @ApiModelProperty(value = "佣金比例")
    private BigDecimal commissionRate;
    @ApiModelProperty(value = "优惠券链接")
    private String couponLink;
    @ApiModelProperty(value = "券总量")
    private Integer couponTotalNum;
    @ApiModelProperty(value = "领券量")
    private Integer couponReceiveNum;
    @ApiModelProperty(value = "优惠券开始时间")
    private String couponStartTime;
    @ApiModelProperty(value = "优惠券结束时间")
    private String couponEndTime;
    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;
    @ApiModelProperty(value = "优惠券使用条件")
    private String couponConditions;
    @ApiModelProperty(value = "30天销量")
    private BigDecimal monthSales;
    @ApiModelProperty(value = "2小时销量")
    private BigDecimal twoHoursSales;
    @ApiModelProperty(value = "当日销量")
    private BigDecimal dailySales;
    @ApiModelProperty(value = "是否是品牌商品：1-是，0-不是")
    private Integer brand;
    @ApiModelProperty(value = "品牌id")
    private Long brandId;
    @ApiModelProperty(value = "品牌名称")
    private String brandName;
    @ApiModelProperty(value = "商品上架时间")
    private String createTime;
    @ApiModelProperty(value = "活动类型，1-无活动，2-淘抢购，3-聚划算")
    private Integer activityType;
    @ApiModelProperty(value = "活动开始时间")
    private String activityStartTime;
    @ApiModelProperty(value = "活动结束时间")
    private String activityEndTime;
    @ApiModelProperty(value = "店铺类型，1-天猫，0-淘宝")
    private Integer shopType;
    @ApiModelProperty(value = "淘宝卖家id")
    private String sellerId;
    @ApiModelProperty(value = "店铺名称")
    private String shopName;
    @ApiModelProperty(value = "淘宝店铺等级")
    private Integer shopLevel;
    @ApiModelProperty(value = "描述分")
    private BigDecimal descScore;
    @ApiModelProperty(value = "描述相符")
    private BigDecimal dsrScore;
    @ApiModelProperty(value = "描述同行比")
    private BigDecimal dsrPercent;
    @ApiModelProperty(value = "服务态度")
    private BigDecimal shipScore;
    @ApiModelProperty(value = "服务同行比")
    private BigDecimal shipPercent;
    @ApiModelProperty(value = "物流服务")
    private BigDecimal serviceScore;
    @ApiModelProperty(value = "物流同行比")
    private BigDecimal servicePercent;
    @ApiModelProperty(value = "热推值")
    private BigDecimal hotPush;
    @ApiModelProperty(value = "放单人名称")
    private String teamName;
    @ApiModelProperty(value = "是否海淘，1-海淘商品，0-非海淘商品")
    private Integer haitao;
    @ApiModelProperty(value = "是否是天猫超市商品：1-是，0-不是")
    private Integer tchaoshi;
    @ApiModelProperty(value = "是否是金牌卖家：1-是，0-非金牌卖家")
    private Integer goldSellers;
    @ApiModelProperty(value = "1.购物津贴；2.跨店满减；0.无")
    private Integer discountType;
    @ApiModelProperty(value = "活动满减的满值")
    private BigDecimal discountFull;
    @ApiModelProperty(value = "活动满减的减值")
    private BigDecimal discountCut;
    @ApiModelProperty(value = "后台配置的活动id")
    private List<Integer> marketGroup;
    @ApiModelProperty(value = "活动信息")
    private List<Activity> activityInfo;

    @Data
    public static class Activity {
        private Integer activityId;
        private String activityName;
    }
}
