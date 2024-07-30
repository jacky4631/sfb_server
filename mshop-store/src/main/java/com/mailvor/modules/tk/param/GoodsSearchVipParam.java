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
@ApiModel(value="GoodsSearchVipParam", description="商品表查询参数")
public class GoodsSearchVipParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "排序字段")
    private String fieldName;

    @ApiModelProperty(value = "排序顺序：0-正序，1-逆序，默认正序")
    private Integer order;

    @ApiModelProperty(value = "分页（从1开始）")
    private Integer page;

    @ApiModelProperty(value = "是否积分兑换商品")
    private String keyword;

    private String openId;
    private String realCall = "true";

}
