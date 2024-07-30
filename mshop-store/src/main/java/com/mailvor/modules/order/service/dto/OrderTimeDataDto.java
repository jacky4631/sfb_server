/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderTimeDataDTO
 * @author huangyu
 * @Date 2019/11/25
 **/
@Data
public class OrderTimeDataDto implements Serializable {

    /**今日成交额*/
    private Double todayPrice;

    /**今日佣金额*/
    private Double todayFee;

    /**今日订单数*/
    private Long todayCount;

    private Double proPrice;  //昨日成交额
    private Double proFee;  //昨日预估佣金
    private Long proCount;//昨日订单数

    /**本月成交额*/
    private Double monthPrice;

    /**本月佣金额*/
    private Double monthFee;

    /**本月订单数*/
    private Long monthCount;

    /**上月成交额*/
    private Double proMonthPrice;

    /**上月佣金额*/
    private Double proMonthFee;

    /**上月订单数*/
    private Long proMonthCount;

    private Long userCount;
    private Long userTodayCount;
    private Long userProCount;
    private Long userMonthCount;
}
