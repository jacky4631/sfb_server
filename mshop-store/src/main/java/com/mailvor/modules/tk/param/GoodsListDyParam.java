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
public class GoodsListDyParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "排序（默认0）：0综合、1销量倒序、2销量升序、3价格倒序、4价格升序")
    private Integer sort;

    @Schema(description = "类目ID")
    private Integer cateId = 0;

    @Schema(description = "价格筛选：起始价（元）")
    private Double priceMin;

    @Schema(description = "价格筛选：结束价（元）")
    private Double priceMax;

    @Schema(description = "销量筛选：起始")
    private Integer salesMin;

    @Schema(description = "销量筛选：结束")
    private Integer salesMax;

}
