/**
 * Copyright (C) 2018-2024
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
@Schema(description="GoodsDetailParam对象")
public class GoodsJdWordParam extends GoodsDetailParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券领取链接，无优惠券为空")
    private String couponLink;

    @Schema(description = "商品原始物料链接(商品详情地址)，转链必填")
    private String materialUrl;
}
