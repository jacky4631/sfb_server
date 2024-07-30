package com.mailvor.modules.energy.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthCardConfigDto {
    /**
     * 月卡价格
     * */
    private BigDecimal price = BigDecimal.valueOf(299.0);
    /**
     * 月卡有效期
     * */
    private Integer expired = 31;

    /*
     * 开通月卡自己可以获取的热度值
     * */
    private Double value = 300d;
    /*
     * 开通月卡上级可以获取的热度值
     * */
    private Double valueOne = 50d;
    /*
     * 开通月卡上上级可以获取的热度值
     * */
    private Double valueTwo = 50d;


}
