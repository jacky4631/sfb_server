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
 * @date 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品表查询参数")
public class GoodsSearchVipParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "排序字段")
    private String fieldName;

    @Schema(description = "排序顺序：0-正序，1-逆序，默认正序")
    private Integer order;

    @Schema(description = "分页（从1开始）")
    private Integer page;

    @Schema(description = "是否积分兑换商品")
    private String keyword;

    private String openId;
    private String realCall = "true";

}
