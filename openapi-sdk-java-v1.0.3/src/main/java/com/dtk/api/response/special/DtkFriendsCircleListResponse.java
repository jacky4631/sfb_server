package com.dtk.api.response.special;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 朋友圈文案响应结果实体
 *
 * @author 1
 * @date 2020/11/12 15:48
 */
@Data
public class DtkFriendsCircleListResponse {
    private BigDecimal quanMLink;
    private BigDecimal hzQuanOver;
    private BigDecimal estimateAmount;
    private String circleText;
    private Integer freeshipRemoteDistrict;
    private Integer id;
    private String goodsId;
    private String title;
    private String dtitle;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
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
    private String createTime;
    private String mainPic;
    private String marketingMainPic;
    private Integer cid;
    private Integer tbcid;
    private BigDecimal discounts;
    private BigDecimal commissionRate;
    private Integer couponTotalNum;
    private String shopName;
    private Integer brand;
    private Long brandId;
    private String brandName;
    private String itemLink;
    private Integer shopType;
    private List<Integer> subcid;
    private String shipaiPic;
    private String shangchaoPic;
}
