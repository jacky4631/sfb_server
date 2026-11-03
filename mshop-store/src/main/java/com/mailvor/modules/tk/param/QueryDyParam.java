/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@Schema(description = "剪切板识别")
public class QueryDyParam {
    private static final long serialVersionUID = 1L;
    @Schema(description = "页大小，默认20，1~100")
    private Integer page=1;
    @Schema(description = "页大小，默认20，1~100")
    private Integer size = 50;

    @Schema(description = "订单时间查询类型(1：下单时间，2：完成时间（购买用户确认收货时间），3：更新时间")
    private Integer data_type = 1;

    @Schema(description = "开始时间 格式yyyy-MM-dd HH:mm:ss")
    private String start_time;
    @Schema(description = "结束时间 格式yyyy-MM-dd HH:mm:ss")
    private String end_time;

//    @Schema(description = "订单渠道类型：1、商品分销订单；2、直播间分销订单")
//    private Integer order_type =1;

    @Schema(description = "取最近多少天的订单")
    private Integer day = 90;
    @Schema(description = "一次取多少分钟的订单")
    private Integer minutes = 60;
}
