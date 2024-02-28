/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@ApiModel(value="QueryDyParam", description="剪切板识别")
public class QueryDyKuParam {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer page=1;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer size = 100;

    @ApiModelProperty(value = "开始时间 格式yyyy-MM-dd HH:mm:ss")
    private Date start;
    @ApiModelProperty(value = "结束时间 格式yyyy-MM-dd HH:mm:ss")
    private Date end;

    @ApiModelProperty(value = "1.物料商品、2.直播商品、3.本地生活团购商品")
    private Integer mediaType = 3;

    @ApiModelProperty(value = "取最近多少天的订单")
    private Integer day = 90;
    @ApiModelProperty(value = "一次取多少分钟的订单")
    private Integer minutes = 60;
}
