/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品表查询参数")
public class GoodsCommentParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "大淘客商品id（id和goodsid其中一个必填）")
    private Integer id;

    @Schema(description = "淘宝商品id（id和goodsid其中一个必填）")
    private String goodsId;

    @Schema(description = "默认：0-全部 评论类型：0-全部；1-含图；2-含视频；")
    private Integer type;

    @Schema(description = "排序方式 0-按热度排序 1-按最新添加排序 默认为0")
    private Integer sort;

    @Schema(description = "评论类型 0-全部 1-去掉默认好评 默认为0(2020/12/30新增字段)")
    private Integer haopingType;

}
