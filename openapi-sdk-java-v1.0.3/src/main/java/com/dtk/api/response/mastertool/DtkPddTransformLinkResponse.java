package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 17:11
 */
@Getter
@Setter
public class DtkPddTransformLinkResponse {
    /**
     * 对应出参mobile_url的短链接，与mobile_url功能一致。
     */
    private String mobileShortUrl;

    /**
     * 使用此推广链接，用户安装拼多多APP的情况下会唤起APP，否则唤起H5页面
     */
    private String mobileUrl;

    /**
     * 对应出参url的短链接，与url功能一致
     */
    private String shortUrl;

    /**
     * 普通推广长链接，唤起H5页面
     */
    private String url;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 最小单买价格（单位为分）
     */
    private BigDecimal minNormalPrice;

    /**
     * 最小拼团价（单位为分）
     */
    private BigDecimal minGroupPrice;

    /**
     * 佣金比例，千分比
     */
    private BigDecimal promotionRate;

    /**
     * 比价行为预判定佣金，需要用户备案
     */
    private BigDecimal predictPromotionRate;

    /**
     * 商品是否有优惠券
     * true-有，
     * false-没有
     */
    private Integer hasCoupon;

    /**
     * 优惠券门槛价格，单位为分
     */
    private BigDecimal couponMinOrderAmount;

    /**
     * 优惠券面额，单位为分
     */
    private BigDecimal couponDiscount;

    /**
     * 优惠券总数量
     */
    private Long couponTotalQuantity;

    /**
     * 优惠券剩余数量
     */
    private Long couponRemainQuantity;

    /**
     * qq小程序信息:小程序path值
     */
    private String qqAppPagePath;

    /**
     * 拼多多福利券微信小程序信息:小程序path值
     */
    private String weAppPagePath;

    /**
     * 优惠券生效时间，UNIX时间戳
     */
    private String couponStartTime;

    /**
     * 优惠券失效时间，UNIX时间戳
     */
    private String couponEndTime;
}
