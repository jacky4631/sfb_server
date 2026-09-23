package com.dtk.api.response.special;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 直播好货响应结果实体
 *
 * @author baige
 * @date 2020/11/12 10:51
 */
@Data
public class DtkLiveMaterialGoodsListResponse {
    private Integer id;
    private String goodsId;
    private String title;
    private String dtitle;
    private String itemLink;
    private String desc;
    private String mainPic;
    private String marketingMainPic;
    private BigDecimal monthSales;
    private BigDecimal twoHoursSales;
    private BigDecimal dailySales;
    private Integer commissionType;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal couponPrice;
    private Integer couponReceiveNum;
    private Integer couponTotalNum;
    private String couponEndTime;
    private String couponStartTime;
    private String couponConditions;
    private Long brandId;
    private String brandName;
    private BigDecimal commissionRate;
    private String liveMaterial;
    private String date;
}
