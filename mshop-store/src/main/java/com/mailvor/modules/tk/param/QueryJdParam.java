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
public class QueryJdParam {
    private static final long serialVersionUID = 1L;
    @Schema(description = "页大小，默认20，1~100")
    private Integer pageNo = 1;
    @Schema(description = "页大小，默认20，1~100")
    private Integer pageSize = 100;

    @Schema(description = "订单时间查询类型(1：下单时间，2：完成时间（购买用户确认收货时间），3：更新时间")
    private Integer type = 1;

    @Schema(description = "开始时间 格式yyyy-MM-dd HH:mm:ss，与endTime间隔不超过1小时")
    private String startTime;
    @Schema(description = "结束时间 格式yyyy-MM-dd HH:mm:ss，与startTime间隔不超过1小时")
    private String endTime;

    @Schema(description = "位点，第一页数据返回里面有个这个字段，查第二页的数据的时候就传过去")
    private Long childUnionId;
    @Schema(description = "支持出参数据筛选，逗号分隔，目前可用：goodsInfo（商品信息）,categoryInfo(类目信息）")
    private String fields;

    @Schema(description = "取最近多少天的订单")
    private Integer day = 60;
    @Schema(description = "一次取多少分钟的订单")
    private Integer minutes = 30;

    private String key;
}
