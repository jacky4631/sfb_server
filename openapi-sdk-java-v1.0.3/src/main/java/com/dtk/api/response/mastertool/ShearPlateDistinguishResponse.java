package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 官方活动会场转链响应结果实体
 *
 * @author:YSH
 * @date: 2021/7/05
 * @time: 09:57
 */
@Getter
@Setter
public class ShearPlateDistinguishResponse {
    private Integer parseStatus;
    private String originContent;
    private Integer sensitiveWords;
    private String platType;
    private String itemId;
    private String itemLink;
    private String itemName;
    private String mainPic;
    private String marketMainPic;
    private Integer monthSales;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private BigDecimal couponPrice;
    private String couponLink;
    private String couponStartTime;
    private String couponEndTime;
    private Integer couponReceiveNum;
    private Integer couponTotalCount;
    private String dataType;
    private String couponId;
    private String tpwd;
    private String longTpwd;
    private String promotionShortUrl;
}
