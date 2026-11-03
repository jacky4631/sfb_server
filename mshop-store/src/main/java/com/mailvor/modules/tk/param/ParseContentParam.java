/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "剪切板识别")
public class ParseContentParam {
    private static final long serialVersionUID = 1L;

    public ParseContentParam(String content) {
        this.content = content;
    }

    @Schema(description = "文本内容")
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
