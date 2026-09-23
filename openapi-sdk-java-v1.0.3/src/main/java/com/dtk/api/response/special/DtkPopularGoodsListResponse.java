package com.dtk.api.response.special;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 每日爆品推荐响应结果实体
 *
 * @author baige
 * @date 2020/11/12 10:59
 */
@Data
public class DtkPopularGoodsListResponse {

    private Integer id;
    private String goodsId;
    private String title;
    private String dtitle;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private Integer shopType;
    private Integer goldSellers;
    private BigDecimal monthSales;
    private BigDecimal twoHoursSales;
    private BigDecimal dailySales;
    private Integer commissionType;
    private String desc;
    private Integer couponReceiveNum;
    private String couponLink;
    private String couponEndTime;
    private String couponStartTime;
    private BigDecimal couponPrice;
    private String couponConditions;
    private Integer activityType;
    private String createTime;
    private String mainPic;
    private String marketingMainPic;
    private String sellerId;
    private Integer cid;
    private Integer tbcid;
    private BigDecimal discounts;
    private BigDecimal commissionRate;
    private Integer couponTotalNum;
    private String activityStartTime;
    private String activityEndTime;
    private String shopName;
    private Integer shopLevel;
    private BigDecimal descScore;
    private Integer brand;
    private Long brandId;
    private String brandName;
    private BigDecimal hotPush;
    private String teamName;
    private String itemLink;
    private Integer tchaoshi;
    private BigDecimal dsrScore;
    private BigDecimal dsrPercent;
    private BigDecimal shipScore;
    private BigDecimal shipPercent;
    private BigDecimal serviceScore;
    private BigDecimal servicePercent;
    private List<Integer> subcid;
    private String video;
    private BigDecimal quanMLink;
    private BigDecimal hzQuanOver;
    private Integer yunfeixian;
    private BigDecimal estimateAmount;
    private Integer freeshipRemoteDistrict;
    private List<String> specialText;
}
