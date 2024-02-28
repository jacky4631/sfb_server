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
@ApiModel(value="MwGoodQueryParam对象", description="商品表查询参数")
public class DyListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别")
    private Integer cateId = 0;

    @ApiModelProperty(value = "分类ID")
    private Integer searchType = 0;

    @ApiModelProperty(value = "分类ID")
    private Integer firstCid = null;



}
