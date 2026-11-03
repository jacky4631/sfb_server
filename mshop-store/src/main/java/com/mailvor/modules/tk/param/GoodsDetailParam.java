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
@Schema(description = "商品详情查询参数")
public class GoodsDetailParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "淘宝商品id")
    private String goodsId;

}
