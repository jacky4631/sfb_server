/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.param;

import com.mailvor.modules.tk.param.BaseParam;
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
@ApiModel(value="ShopDetailParam对象", description="商品表查询参数")
public class ShopDetailParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "平台")
    private String platform;
    @ApiModelProperty(value = "uid")
    private Long uid;

}
