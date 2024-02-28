/**
 * Copyright (C) 2018-2022
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
public class IncomeDataDto implements Serializable {
    private Double todayShopIn;
    private Double monthShopIn;
    private Double proMonthShopIn;
    private Double todayExtractOut;
    private Double monthExtractOut;
    private Double proMonthExtractOut;

    private Double todayOtherOut;
    private Double monthOtherOut;
    private Double proMonthOtherOut;

    private Double todayIn;
    private Double monthIn;
    private Double proMonthIn;
}
