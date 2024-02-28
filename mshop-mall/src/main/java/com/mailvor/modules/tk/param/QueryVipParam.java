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
@ApiModel(value="QueryJdParam", description="剪切板识别")
public class QueryVipParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "页码：从1开始")
    private Integer page = 1;
    @ApiModelProperty(value = "页面大小：默认20")
    private Integer pageSize = 20;

    @ApiModelProperty(value = "订单时间起始 时间戳 单位毫秒")
    private String orderTimeStart;
    @ApiModelProperty(value = "订单时间结束 时间戳 单位毫秒")
    private String orderTimeEnd;



    @ApiModelProperty(value = "取最近多少天的订单")
    private Integer day = 60;
    @ApiModelProperty(value = "一次取多少分钟的订单")
    private Integer minutes = 30;
}
