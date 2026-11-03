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
 * @date 2022-02-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "官方活动参数")
public class TbActivityListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "输出的端口类型：0.全部（默认），1.PC，2.无线")
    private Integer type;

}
