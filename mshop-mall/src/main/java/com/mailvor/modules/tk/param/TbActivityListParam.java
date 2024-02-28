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
 * @date 2022-02-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="GoodsTbTopicListParam", description="官方活动参数")
public class TbActivityListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "输出的端口类型：0.全部（默认），1.PC，2.无线")
    private Integer type;

}
