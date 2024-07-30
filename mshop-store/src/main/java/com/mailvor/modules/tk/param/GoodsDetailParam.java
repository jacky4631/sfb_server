/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value="MwGoodsDetailQueryParam对象", description="商品详情查询参数")
public class GoodsDetailParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;

}
