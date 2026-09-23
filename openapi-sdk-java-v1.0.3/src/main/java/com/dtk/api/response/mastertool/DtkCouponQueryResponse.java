package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 17:36
 */
@Getter
@Setter
public class DtkCouponQueryResponse {

    /**
     * 优惠券id
     */
    private String couponId = "";

    /**
     * 优惠券面额
     */
    private BigDecimal couponAmount = BigDecimal.ZERO;

    /**
     * 优惠券总量
     */
    private Integer couponTotalNum = 0;

    /**
     * 优惠券已领取量
     */
    private Integer couponReceiveNum = 0;

    /**
     * 优惠券剩余量
     */
    private Integer couponRemainNum = 0;

    /**
     * 优惠券使用条件
     */
    private String couponConditions = "";

    /**
     * 优惠券开始时间
     */
    private String couponStartTime = "";

    /**
     * 优惠券结束时间
     */
    private String couponEndTime = "";

    /**
     * 优惠券链接
     */
    private String couponLink = "";
}
