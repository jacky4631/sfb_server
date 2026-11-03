/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "查询参数对象")
public class BaseParam implements Serializable{
    private static final long serialVersionUID = -3263921252635611410L;

    @Schema(description = "页码,默认为1")
	private Integer pageId =1;
	@Schema(description = "页大小,默认为10")
	private Integer pageSize = 10;
    @Schema(description = "搜索字符串")
    private String keyword;

}
