/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@Schema(description = "剪切板识别")
public class QueryDyKuParam {
    private static final long serialVersionUID = 1L;
    @Schema(description = "页大小，默认20，1~100")
    private Integer page=1;
    @Schema(description = "页大小，默认20，1~100")
    private Integer size = 100;

    @Schema(description = "开始时间 格式yyyy-MM-dd HH:mm:ss")
    private Date start;
    @Schema(description = "结束时间 格式yyyy-MM-dd HH:mm:ss")
    private Date end;

    @Schema(description = "1.物料商品、2.直播商品、3.本地生活团购商品")
    private Integer mediaType = 3;

    @Schema(description = "取最近多少天的订单")
    private Integer day = 90;
    @Schema(description = "一次取多少分钟的订单")
    private Integer minutes = 60;
}
