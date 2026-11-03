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
@Schema(description = "")
public class QueryPddParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否为礼金订单，查询礼金订单时，订单类型不填（默认推广订单）。")
    private Boolean cashGiftOrder;

    @Schema(description = "页大小，默认20，1~100")
    private Integer pageSize = 100;

    @Schema(description = "订单类型：1-推广订单；2-直播间订单")
    private Integer queryOrderType;

    @Schema(description = "开始时间 格式yyyy-MM-dd HH:mm:ss，与endTime间隔不超过1小时")
    private String startTime;
    @Schema(description = "结束时间 格式yyyy-MM-dd HH:mm:ss，与startTime间隔不超过1小时")
    private String endTime;

    @Schema(description = "上一次的迭代器id(第一次不填)")
    private String lastOrderId;

    @Schema(description = "取最近多少天的订单")
    private Integer day = 90;
    @Schema(description = "一次取多少分钟的订单")
    private Integer minutes = 180;
}
