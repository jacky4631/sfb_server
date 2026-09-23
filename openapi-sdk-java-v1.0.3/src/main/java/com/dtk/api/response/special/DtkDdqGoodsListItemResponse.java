package com.dtk.api.response.special;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 咚咚抢响应结果数据项实体
 *
 * @author baige
 * @date 2020/11/12 11:23
 */
@Data
public class DtkDdqGoodsListItemResponse {
    private Integer id;
    private String goodsId;
    private String itemLink;
    private String title;
    private String dtitle;
    private Integer cid;
    private Integer tbcid;
    private List<Integer> subcid;
    private String ddqDesc;
    private String mainPic;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal couponPrice;
    private BigDecimal discounts;
    private String couponLink;
    private String couponEndTime;
    private String couponStartTime;
    private String couponConditions;
    private Integer commissionType;
    private BigDecimal commissionRate;
    private String createTime;
    private Integer couponReceiveNum;
    private Integer couponTotalNum;
    private BigDecimal monthSales;
    private Integer activityType;
    private String activityStartTime;
    private String activityEndTime;
    private String shopName;
    private Integer shopLevel;
    private String sellerId;
    private Integer brand;
    private Long brandId;
    private String brandName;
    private BigDecimal twoHoursSales;
    private BigDecimal dailySales;
    private BigDecimal quanMLink;
    private BigDecimal hzQuanOver;
    private Integer yunfeixian;
    private BigDecimal estimateAmount;
    private String marketingMainPic;
    private Integer tchaosi;
    private Integer haitao;
    private Integer shoptype;
    private List<String> specialText;
    private List<Integer> marketGroup;
    private Integer lowest;
}
