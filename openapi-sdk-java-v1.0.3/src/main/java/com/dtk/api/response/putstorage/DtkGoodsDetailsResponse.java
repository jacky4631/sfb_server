package com.dtk.api.response.putstorage;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 单品详情响应结果实体
 *
 * @author 1
 * @date 2020/11/10 15:54
 */
@Getter
@Setter
public class DtkGoodsDetailsResponse {

    private String imgs;
    private String reimgs;
    /**
     * 定金 无则显示0
     */
    private BigDecimal quanMLink = BigDecimal.ZERO;
    /**
     * 立减 无则显示0
     */
    private BigDecimal hzQuanOver = BigDecimal.ZERO;
    /**
     * 0.不包运费险  1.包运费险
     */
    private Integer yunfeixian;
    /**
     * 预估淘礼金
     */
    private BigDecimal estimateAmount = BigDecimal.ZERO;
    private String shopLogo;
    /**
     * 商品卖点
     */
    private List<String> specialText;
    /**
     * 是否偏远地区包邮(0.不包  1.包)
     */
    private Integer freeshipRemoteDistrict;
    /**
     * 商品视频
     */
    private String video;
    private String detailPics;
    /**
     * 是否细分排行榜商品：0不是，1是
     */
    private Integer isSubdivision = 0;

    /**
     * 细分排行分类id
     */
    private Integer subdivisionId;

    /**
     * 细分排行分类名称
     */
    private String subdivisionName;

    /**
     * 细分排行榜排行名次
     */
    private Integer subdivisionRank;

    /**
     * 定向佣金类型
     */
    private Integer directCommissionType;

    /**
     * 定向佣金
     */
    private BigDecimal directCommission;

    /**
     * 定向佣金链接地址
     */
    private String directCommissionLink;

    /**
     * 商品id
     */
    private Integer id;

    /**
     * 淘宝商品id
     */
    @JsonInclude()
    private String goodsId;

    /**
     * 淘宝加密商品id
     */
    @JsonInclude()
    private String goodsSign;

    /**
     * 淘宝标题
     */
    private String title;

    /**
     * 短标题
     */
    private String dtitle;

    /**
     * 原价
     */
    private BigDecimal originalPrice = BigDecimal.ZERO;

    /**
     * 实际价格
     * 可能计算为:原价-券金额
     */
    private BigDecimal actualPrice = BigDecimal.ZERO;

    /**
     * 商家类型 1是天猫 0 是淘宝
     */
    private Integer shopType;

    /**
     * 是否是金牌卖家
     */
    private Integer goldSellers;

    /**
     * 30天销量
     */
    private BigDecimal monthSales;

    /**
     * 两小时销量
     */
    private BigDecimal twoHoursSales;

    /**
     * 当天销量
     */
    private BigDecimal dailySales;


    /**
     * 佣金类型 通用 0   定向 1  高佣 2  营销计划 3
     */
    private Integer commissionType;


    /**
     * 文案
     */
    private String desc;

    /**
     * 已经领券的数量
     */
    private Integer couponReceiveNum;

    /**
     * 券的链接地址
     */
    private String couponLink;

    /**
     * 券结束时间
     */
    private String couponEndTime;

    /**
     * 券开始时间
     */
    private String couponStartTime;

    /**
     * 券金额
     */
    private BigDecimal couponPrice = BigDecimal.ZERO;

    /**
     * 券使用条件
     */
    private String couponConditions;

    /**
     * 2淘抢购 3聚划算
     */
    private Integer activityType;
    /**
     * 商品上架时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 团长链接，已经转好了的链接地址
     */
    @JsonIgnore
    private String teamLink;

    /**
     * 主图
     */
    private String mainPic;


    /**
     * 营销主图
     */
    private String marketingMainPic;


    /**
     * 淘宝卖家的id
     */
    private String sellerId;

    private String brandWenan;

    /**
     * 我们自己的分类id
     */
    private Integer cid;

    /**
     * 大淘客二级分类
     */
    @JsonAlias("scid")
    @JsonProperty("tbcid")
    private Integer scid;

    /**
     * 折扣力度
     */
    private BigDecimal discounts;

    /**
     * 佣金比率
     */
    private BigDecimal commissionRate;

    /**
     * 券的总数目
     */
    private Integer couponTotalNum;

    /**
     * 是否是海涛
     */
    private Integer haitao;

    /**
     * 活动结束时间
     */
    private String activityStartTime;

    /**
     * 活动开始时间
     */
    private String activityEndTime;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 淘宝店铺等级
     */
    private Integer shopLevel;

    /**
     * 描述分
     */
    private BigDecimal descScore;

    /**
     * 是否是品牌 1是0不是
     */
    private Integer brand;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 热推值
     */
    private BigDecimal hotPush;

    /**
     * 放单人名称
     */
    private String teamName;

    /**
     * 淘宝链接
     */
    private String itemLink;

    /**
     * 是否是超市 1是0不是
     */
    private Integer tchaoshi;

    /**
     * 商品详情图片
     * 只提供反序列化，不提供序列化
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String detailPic;

    /**
     * 描述行业评分
     */
    private BigDecimal dsrScore;


    /**
     * 评分对比
     */
    private BigDecimal dsrPercent;

    /**
     * 物流评分
     */
    private BigDecimal shipScore;

    /**
     * 物流评分对比
     */
    private BigDecimal shipPercent;

    /**
     * 服务评分
     */
    private BigDecimal serviceScore;

    @ApiModelProperty(value = "单单有奖活动id")
    private String activityId;

    @ApiModelProperty(value = "单单有奖金额")
    private BigDecimal cpaRewardAmount;

    /**
     * 服务评分对比
     */
    private BigDecimal servicePercent;

    /**
     * 二级分类集合
     */
    private List<Integer> subcid;

    /**
     * 24小时销量
     */
    private Integer sales24h;

    /**
     * 是否近30天历史最低价，0-否；1-是
     */
    private Integer lowest;

    private String couponId;

    @ApiModelProperty(value = "是否已经验货，0-否；1-是")
    private Integer inspectedGoods;
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
