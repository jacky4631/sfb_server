package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "GoodsSearchVo对象", description = "大淘客搜索商品")
public class GoodsSearchVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Long id;

    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;

    @ApiModelProperty(value = "新商品id，每次转链后都会变化")
    private String goodsSign;

    @ApiModelProperty(value = "淘宝标题")
    private String title;

    @ApiModelProperty(value = "短标题")
    private String dtitle;

    @ApiModelProperty(value = "商品原价")
    private Double originalPrice;

    @ApiModelProperty(value = "券后价")
    private Double actualPrice;

    @ApiModelProperty(value = "店铺类型，1-天猫，0-淘宝")
    private Integer shopType;

    @ApiModelProperty(value = "30天热销")
    private Long monthSales;

    @ApiModelProperty(value = "2小时热销")
    private Long twoHoursSales;

    @ApiModelProperty(value = "当天热销")
    private Long dailySales;

    @ApiModelProperty(value = "佣金类型，0-通用，1-定向，2-高佣，3-营销计划")
    private Integer commissionType;

    @ApiModelProperty(value = "推广文案")
    private String desc;

    @ApiModelProperty(value = "领券量")
    private Long couponReceiveNum;

    @ApiModelProperty(value = "优惠券链接")
    private String couponLink;

    @ApiModelProperty(value = "优惠券结束时间")
    private String couponEndTime;

    @ApiModelProperty(value = "优惠券开始时间")
    private String couponStartTime;

    @ApiModelProperty(value = "优惠券金额")
    private Double couponPrice;

    @ApiModelProperty(value = "优惠券使用条件")
    private String couponConditions;

    @ApiModelProperty(value = "活动类型，1-无活动，2-淘抢购，3-聚划算")
    private Integer activityType;

    @ApiModelProperty(value = "商品上架时间")
    private String createTime;

    @ApiModelProperty(value = "商品主图链接")
    private String mainPic;

    @ApiModelProperty(value = "营销主图链接")
    private String marketingMainPic;

    @ApiModelProperty(value = "淘宝卖家id")
    private String sellerId;

    @ApiModelProperty(value = "商品在大淘客的分类id")
    private Long cid;

    @ApiModelProperty(value = "商品在大淘客的二级分类id")
    private List<Long> subcid;

    @ApiModelProperty(value = "商品在淘宝的分类id")
    private Long tbcid;

    @ApiModelProperty(value = "折扣力度")
    private Double discounts;

    @ApiModelProperty(value = "佣金比例")
    private Double commissionRate;

    @ApiModelProperty(value = "券总量")
    private Long couponTotalNum;

    @ApiModelProperty(value = "活动开始时间")
    private String activityStartTime;

    @ApiModelProperty(value = "活动结束时间")
    private String activityEndTime;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "淘宝店铺等级")
    private Integer shopLevel;

    @ApiModelProperty(value = "描述分")
    private Double descScore;

    @ApiModelProperty(value = "描述相符")
    private Double dsrScore;

    @ApiModelProperty(value = "描述同行比")
    private Double dsrPercent;

    @ApiModelProperty(value = "物流服务")
    private Double shipScore;

    @ApiModelProperty(value = "物流同行比")
    private Double shipPercent;

    @ApiModelProperty(value = "服务态度")
    private Double serviceScore;

    @ApiModelProperty(value = "服务同行比")
    private Double servicePercent;

    @ApiModelProperty(value = "是否是品牌商品")
    private Integer brand;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "热推值")
    private Long hotPush;

    @ApiModelProperty(value = "放单人名称")
    private String teamName;

    @ApiModelProperty(value = "商品淘宝链接")
    private String itemLink;

    @ApiModelProperty(value = "定金，若无定金则显示0")
    private Double quanMLink;

    @ApiModelProperty(value = "立减，若无立减金额则显示0")
    private Double hzQuanOver;

    @ApiModelProperty(value = "0.不包运费险 1.包运费险")
    private Integer yunfeixian;

    @ApiModelProperty(value = "预估淘礼金")
    private Double estimateAmount;

    @ApiModelProperty(value = "偏远地区包邮，0.不包邮，1.包邮")
    private Integer freeshipRemoteDistrict;

    @ApiModelProperty(value = "1.购物津贴；2.跨店满减；0.无")
    private Integer discountType;

    @ApiModelProperty(value = "活动满减的满值")
    private Double discountFull;

    @ApiModelProperty(value = "活动满减的减值")
    private Double discountCut;

    @ApiModelProperty(value = "热门活动ID")
    private String marketGroup;

    @ApiModelProperty(value = "活动信息")
    private List<GoodsActivityInfoVo> activityInfo;

    @ApiModelProperty(value = "商品是否已经验货，0-否；1-是")
    private Integer inspectedGoods;

    @ApiModelProperty(value = "店铺logo")
    private String shopLogo;
}
