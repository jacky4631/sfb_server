package com.dtk.api.response.special;

import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 16:29
 */
@Getter
@Setter
public class DtkMostPopularCommodityListResponse {
    private String id;
    private String goodsId;
    private String zsPic;
    private String zsName;
    private String mainPic;
    private String itemLink;
    private String commissionRate;
    private String shopType;
    private String monthSale;
    private String dailySales;
    private String originalPrice;
    private String actualPrice;
    private String couponPrice;
    private String couponReceiveNum;
    private String couponStartTime;
    private String couponEndTime;
    private String couponConditions;
    private String showTag;
}
