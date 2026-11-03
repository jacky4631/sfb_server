package com.mailvor.modules.activity.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SeckillTimeDto {

    private Integer id;
    /**
     * 00:00
     */
    @Schema(description = "秒杀产品时间：00:00")
    private String time;

    /**
     *状态
     */
    @Schema(description = "秒杀产品状态显示中文值")
    private String state;

    @Schema(description = "秒杀产品状态")
    private Integer status;

    @Schema(description = "秒杀产品停止时间")
    private Integer stop;
}
