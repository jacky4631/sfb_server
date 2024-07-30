package com.mailvor.modules.energy.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpCardConfigDto {
    /**
     * 体验卡价格
     * */
    private BigDecimal price = BigDecimal.valueOf(19.9);
    /**
     * 体验卡时长
     * */
    private Integer expired = 7;

    /**
     * 体验卡1级分佣比例
     * */
    private String one = "25";
    /**
     * 体验卡2级分佣比例
     * */
    private String two = "20";

    /**
     * 订单最小分佣比例
     * */
    private BigDecimal orderMin = BigDecimal.valueOf(40);

    /**
     * 订单最大分佣比例
     * */
    private BigDecimal orderMax = BigDecimal.valueOf(50);
    /**
     * 每天产生的最少订单数量
     * */
    private Integer min = 20;
    /**
     * 每天产生的最多订单数量
     * */
    private Integer max = 40;

    /**
     * 最少解锁天数
     * */
    private Integer unlockMin = 5;
    /**
     * 最多解锁天数
     * */
    private Integer unlockMax = 7;


}
