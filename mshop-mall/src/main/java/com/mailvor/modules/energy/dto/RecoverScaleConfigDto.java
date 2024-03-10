package com.mailvor.modules.energy.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecoverScaleConfigDto {

    private BigDecimal monthRecoverScale = BigDecimal.valueOf(6.66);
    /**
     * 月卡翻倍比例
     * */
    private BigDecimal monthScale = BigDecimal.valueOf(1.80);
    /** 月卡翻倍失效天数，相比create_time */
    private Integer monthInvalidDay = 60;

}
