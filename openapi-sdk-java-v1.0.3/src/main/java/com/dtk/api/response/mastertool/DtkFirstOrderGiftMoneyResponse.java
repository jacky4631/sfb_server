package com.dtk.api.response.mastertool;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首单礼金商品响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:17
 */
@Getter
@Setter
public class DtkFirstOrderGiftMoneyResponse {

    /**
     * 商品id
     */
    private Integer id;

    /**
     * 淘宝商品id
     */
    private String goodsId;

    /**
     * 淘宝标题
     */
    private String title;

    /**
     * 短标题
     */
    private String dtitle;

    /**
     * 原价，对应数据库字段
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
    private String createTime;


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

    /**
     * 服务评分对比
     */
    private BigDecimal servicePercent;

    /**
     * 二级分类集合
     */
    private List<Integer> subcid;

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
    /**
     * 首单礼金金额
     */
    private String firstOrderAmount;
    /**
     * 到手价
     */
    private BigDecimal estimatePrice;


}
