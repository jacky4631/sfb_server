/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName ProductFormatDTO
 * @author huangyu
 * @Date 2019/10/12
 **/

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductFormatDto {

    private String sku = "";

    @JsonProperty("bar_code")
    private String barCode = "";

    private Double brokerage = 0d;

    @JsonProperty("brokerage_two")
    private Double brokerageTwo = 0d;

    private Double price = 0d;

    @JsonProperty("ot_price")
    private Double otPrice = 0d;

    private Double cost = 0d;

    private Integer stock = 0;

    private Integer integral = 0;

    private String pic = "";

    private String value1 = "";

    private String value2 = "";

    private Double volume = 0d;

    private Double weight = 0d;
    @JsonProperty("pink_price")
    private Double pinkPrice = 0d;
    @JsonProperty("pink_stock")
    private Integer pinkStock = 0;
    @JsonProperty("seckill_price")
    private Double seckillPrice = 0d;
    @JsonProperty("seckill_stock")
    private Integer seckillStock = 0;

    private Map<String, String> detail;

}
