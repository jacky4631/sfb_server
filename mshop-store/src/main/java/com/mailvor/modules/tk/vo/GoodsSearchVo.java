package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 大淘客搜索商品 查询结果对象
 * 对应接口：/api/goods/get-dtk-search-goods
 * </p>
 *
 * @author shenji
 */
@Data
@Schema(description = "大淘客搜索商品")
public class GoodsSearchVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品id")
    private Long id;

    @Schema(description = "淘宝商品id")
    private String goodsId;

    @Schema(description = "新商品id，每次转链后都会变化")
    private String goodsSign;

    @Schema(description = "淘宝标题")
    private String title;

    @Schema(description = "短标题")
    private String dtitle;

    @Schema(description = "商品原价")
    private Double originalPrice;

    @Schema(description = "券后价")
    private Double actualPrice;

    @Schema(description = "店铺类型，1-天猫，0-淘宝")
    private Integer shopType;

    @Schema(description = "30天热销")
    private Long monthSales;

    @Schema(description = "2小时热销")
    private Long twoHoursSales;

    @Schema(description = "当天热销")
    private Long dailySales;

    @Schema(description = "佣金类型，0-通用，1-定向，2-高佣，3-营销计划")
    private Integer commissionType;

    @Schema(description = "推广文案")
    private String desc;

    @Schema(description = "领券量")
    private Long couponReceiveNum;

    @Schema(description = "优惠券链接")
    private String couponLink;

    @Schema(description = "优惠券结束时间")
    private String couponEndTime;

    @Schema(description = "优惠券开始时间")
    private String couponStartTime;

    @Schema(description = "优惠券金额")
    private Double couponPrice;

    @Schema(description = "优惠券使用条件")
    private String couponConditions;

    @Schema(description = "活动类型，1-无活动，2-淘抢购，3-聚划算")
    private Integer activityType;

    @Schema(description = "商品上架时间")
    private String createTime;

    @Schema(description = "商品主图链接")
    private String mainPic;

    @Schema(description = "营销主图链接")
    private String marketingMainPic;

    @Schema(description = "淘宝卖家id")
    private String sellerId;

    @Schema(description = "商品在大淘客的分类id")
    private Long cid;

    @Schema(description = "商品在大淘客的二级分类id")
    private List<Long> subcid;

    @Schema(description = "商品在淘宝的分类id")
    private Long tbcid;

    @Schema(description = "折扣力度")
    private Double discounts;

    @Schema(description = "佣金比例")
    private Double commissionRate;

    @Schema(description = "券总量")
    private Long couponTotalNum;

    @Schema(description = "活动开始时间")
    private String activityStartTime;

    @Schema(description = "活动结束时间")
    private String activityEndTime;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "淘宝店铺等级")
    private Integer shopLevel;

    @Schema(description = "描述分")
    private Double descScore;

    @Schema(description = "描述相符")
    private Double dsrScore;

    @Schema(description = "描述同行比")
    private Double dsrPercent;

    @Schema(description = "物流服务")
    private Double shipScore;

    @Schema(description = "物流同行比")
    private Double shipPercent;

    @Schema(description = "服务态度")
    private Double serviceScore;

    @Schema(description = "服务同行比")
    private Double servicePercent;

    @Schema(description = "是否是品牌商品")
    private Integer brand;

    @Schema(description = "品牌id")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "热推值")
    private Long hotPush;

    @Schema(description = "放单人名称")
    private String teamName;

    @Schema(description = "商品淘宝链接")
    private String itemLink;

    @Schema(description = "定金，若无定金则显示0")
    private Double quanMLink;

    @Schema(description = "立减，若无立减金额则显示0")
    private Double hzQuanOver;

    @Schema(description = "0.不包运费险 1.包运费险")
    private Integer yunfeixian;

    @Schema(description = "预估淘礼金")
    private Double estimateAmount;

    @Schema(description = "偏远地区包邮，0.不包邮，1.包邮")
    private Integer freeshipRemoteDistrict;

    @Schema(description = "1.购物津贴；2.跨店满减；0.无")
    private Integer discountType;

    @Schema(description = "活动满减的满值")
    private Double discountFull;

    @Schema(description = "活动满减的减值")
    private Double discountCut;

    @Schema(description = "热门活动ID")
    private String marketGroup;

    @Schema(description = "活动信息")
    private List<GoodsActivityInfoVo> activityInfo;

    @Schema(description = "商品是否已经验货，0-否；1-是")
    private Integer inspectedGoods;

    @Schema(description = "店铺logo")
    private String shopLogo;
}
