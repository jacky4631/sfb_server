package com.dtk.api.response.special.subranking;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 各大榜单响应结果基础信息实体
 *
 * @author baige
 * @date 2020/11/12 17:57
 */
@Data
public class DtkRankingBaseResponse {
    private Integer id;
    private String goodsId;
    private String dtitle;
    private BigDecimal actualPrice;
    private BigDecimal commissionRate;
    private BigDecimal couponPrice;
    private Integer couponReceiveNum;
    private Integer couponTotalNum;
    private BigDecimal monthSales;
    private BigDecimal twoHoursSales;
    private BigDecimal dailySales;
    private String mainPic;
    private String title;
    private String desc;
    private BigDecimal originalPrice;
    private String couponLink;
    private String couponStartTime;
    private String couponEndTime;
    private Integer commissionType;
    private String createTime;
    private Integer activityType;
    private Integer shopType;
    private String couponConditions;
    private String sellerId;
    private String shopName;
    private BigDecimal quanMLink;
    private BigDecimal hzQuanOver;
    private Integer yunfeixian;
    private Integer freeshipRemoteDistrict;
    private Integer haitao;
    private List<String> specialText;
}
