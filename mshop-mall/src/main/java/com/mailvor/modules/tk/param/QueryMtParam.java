/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    private Integer page = 1;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer size = 100;

    /**
     * 1	按照子订单支付时间查询
     * 2	按照子订单核验时间查询（注: 等同于startVerifyDate和endVerifyDate查询，切勿混用）
     * 3	按照子订单结算时间查询
     * 4	按照子订单账期时间查询
     * */
    @ApiModelProperty(value = "订单查询时间区间类型")
    private Integer queryType = 1;

    @ApiModelProperty(value = "开始时间 格式yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @ApiModelProperty(value = "结束时间 格式yyyy-MM-dd HH:mm:ss")
    private String endTime;


    @ApiModelProperty(value = "取最近多少天的订单")
    private Integer day = 180;
    @ApiModelProperty(value = "一次取多少分钟的订单")
    private Integer minutes = 60*24;
}
