package com.mailvor.modules.energy.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecoverScaleConfigDto {
    /**
     * 回本比例
     * */
    private BigDecimal expRecoverScale = BigDecimal.valueOf(10.00);


    private BigDecimal monthRecoverScale = BigDecimal.valueOf(6.66);
    /**
     * 体验翻倍比例
     * */
    private BigDecimal expScale = BigDecimal.valueOf(2.10);
    /**
     * 月卡翻倍比例
     * */
    private BigDecimal monthScale = BigDecimal.valueOf(1.80);


    /** 体验翻倍失效天数，相比create_time */
    private Integer expInvalidDay = 15;
    /** 月卡翻倍失效天数，相比create_time */
    private Integer monthInvalidDay = 60;

}
