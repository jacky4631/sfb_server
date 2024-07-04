/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@ApiModel(value="QueryMtParam", description="")
public class QueryMtParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "页数，从1开始")
    private Integer platform;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private List<Integer> businessLine;

    @ApiModelProperty(value = "页数，从1开始")
    private String scrollId;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer size = 100;

    @ApiModelProperty(value = "开始时间 格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间 格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    @ApiModelProperty(value = "取最近多少天的订单")
    private Integer day = 180;
    @ApiModelProperty(value = "一次取多少分钟的订单")
    private Integer minutes = 60*2;
}
