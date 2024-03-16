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
@ApiModel(value="GoodsListDyParam", description="商品表查询参数")
public class GoodsListDyParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "排序（默认0）：0综合、1销量倒序、2销量升序、3价格倒序、4价格升序")
    private Integer sort;

    @ApiModelProperty(value = "类目ID")
    private Integer cateId = 0;

    @ApiModelProperty(value = "价格筛选：起始价（元）")
    private Double priceMin;

    @ApiModelProperty(value = "价格筛选：结束价（元）")
    private Double priceMax;

    @ApiModelProperty(value = "销量筛选：起始")
    private Integer salesMin;

    @ApiModelProperty(value = "销量筛选：结束")
    private Integer salesMax;

}
