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
@ApiModel(value="GoodsSearchParam", description="商品表查询参数")
public class GoodsSearchParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关键词搜索")
    private String keyWords;

    @ApiModelProperty(value = "排序字段，默认为0，0-综合排序，1-商品上架时间从新到旧，2-热销从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort = "0";

    @ApiModelProperty(value = "价格（券后价）下限")
    private Integer priceLowerLimit;

    @ApiModelProperty(value = "价格（券后价）上限")
    private Integer priceUpperLimit;

    @ApiModelProperty(value = "最低佣金比率")
    private Integer commissionRateLowerLimit;

    @ApiModelProperty(value = "商品是否有券（0 - 全部商品，1 - 有券商品，-1 - 无券商品，默认为1）")
    private Integer hasCoupon;

}
