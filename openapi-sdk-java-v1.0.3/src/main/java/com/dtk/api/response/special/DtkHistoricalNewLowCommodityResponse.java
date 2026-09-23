package com.dtk.api.response.special;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/9
 * @time: 10:30
 */
@Getter
@Setter
public class DtkHistoricalNewLowCommodityResponse {
    private String id;
    private String goodsId;
    private String itemLink;
    private String title;
    private String dtitle;
    private String originalPrice;
    private String actualPrice;
    private String shopType;
    private String monthSales;
    private String twoHoursSales;
    private String dailySales;
    private String commissionType;
    private String desc;
    private String couponReceiveNum;
    private String couponLink;
    private String couponEndTime;
    private String couponStartTime;
    private String couponPrice;
    private String couponConditions;
    private String activityType;
    private String createTime;
    private String mainPic;
    private String marketingMainPic;
    private String video;
    private String sellerId;
    private String cid;
    private String tbcid;
    private String discounts;
    private String commissionRate;
    private String couponTotalNum;
    private String activityStartTime;
    private String activityEndTime;
    private String shopName;
    private String shopLevel;
    private String descScore;
    private String dsrScore;
    private String dsrPercent;
    private String shipScore;
    private String shipPercent;
    private String serviceScore;
    private String servicePercent;
    private String brandId;
    private String brandName;
    private String hotPush;
    private String teamName;
    private String quanMLink;
    private String hzQuanOver;
    private String yunfeixian;
    private String estimateAmount;
    private String tchaoshi;
    private String goldSellers;
    private String freeshipRemoteDistrict;
    private List<String> specialText;

}
