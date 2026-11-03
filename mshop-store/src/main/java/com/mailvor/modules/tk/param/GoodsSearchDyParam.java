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
public class GoodsSearchDyParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "召回结果排序条件，0默认排序1历史销量排序2价格排序3佣金金额排序4佣金比例排序；")
    private Integer searchType;

    @Schema(description = "排序顺序（0升序1降序）")
    private Integer sortType;

    @Schema(description = "分页（从1开始）")
    private Integer page;

    @Schema(description = "是否积分兑换商品")
    private String title;

    @Schema(description = "用户appkey")
    private String appkey;
    @Schema(description = "筛选普通佣金率区间-最小值 （乘100，例如10%为10）")
    private Integer cosRatioMin;
    @Schema(description = "筛选普通佣金率区间-最大值（乘100，例如10%为10")
    private Integer cosRatioMax;

}
