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
public class GoodsSearchDyParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "召回结果排序条件，0默认排序1历史销量排序2价格排序3佣金金额排序4佣金比例排序；")
    private Integer searchType;

    @ApiModelProperty(value = "排序顺序（0升序1降序）")
    private Integer sortType;

    @ApiModelProperty(value = "分页（从1开始）")
    private Integer page;

    @ApiModelProperty(value = "是否积分兑换商品")
    private String title;

    @ApiModelProperty(value = "用户appkey")
    private String appkey;
    @ApiModelProperty(value = "筛选普通佣金率区间-最小值 （乘100，例如10%为10）")
    private Integer cosRatioMin;
    @ApiModelProperty(value = "筛选普通佣金率区间-最大值（乘100，例如10%为10")
    private Integer cosRatioMax;

}
