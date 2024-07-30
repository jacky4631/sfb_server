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
public class RankingListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别")
    private Integer rankType;

    @ApiModelProperty(value = "分类ID")
    private Integer cid;

    @ApiModelProperty(value = "是否新品")
    private Integer pageSize;

    @ApiModelProperty(value = "是否积分兑换商品")
    private Integer pageId;
}
