package com.dtk.api.response.special;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品历史券后价响应结果实体
 *
 * @author baige
 * @date 2020/11/12 10:22
 */
@Data
public class DtkPriceTrendResponse {
    private Integer id;
    private String goodsId;
    private String title;
    private String dtitle;
    private BigDecimal originalPrice;
    private List<DtkPriceTrendHistoricalPriceResponse> historicalPrice;
    private BigDecimal monthSales;
    private BigDecimal twoHoursSales;
    private BigDecimal dailySales;
    private Integer commissionType;
    private String desc;
    private Integer couponReceiveNum;
    private String couponEndTime;
    private String couponStartTime;
    private BigDecimal couponPrice;
    private String couponConditions;
    private String mainPic;
    private String marketingMainPic;
    private BigDecimal commissionRate;
    private Integer couponTotalNum;
    private Long brandId;
    private String brandName;
    private String itemLink;
}
