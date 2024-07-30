/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value="ParseContentParam", description="剪切板识别")
public class ParseContentParam {
    private static final long serialVersionUID = 1L;

    public ParseContentParam(String content) {
        this.content = content;
    }

    @ApiModelProperty(value = "文本内容")
    @NotBlank
    private String content;

    private String TbPid;
    private String TbChannelId;

    private String JdUnionId;
    private String JdPid;
    private String jdPositionId;

    private String PddPid;
    private String customerParameters;
}
