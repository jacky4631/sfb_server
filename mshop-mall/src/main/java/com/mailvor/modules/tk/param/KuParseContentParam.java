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
import java.io.Serializable;

/**
 *
 *
 * @author shenji
 * @date 2022-02-20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value="KuParseContentParam", description="好单库剪切板识别")
public class KuParseContentParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String method = "analyze.clipboard";
    @ApiModelProperty(value = "文本内容")
    @NotBlank
    private String content;
    private Integer isChange=0;

    private String tbName;
    private String tbPid;
    private String tbRId;

    private String jdUnionId;
    private String jdPid;

    private String pddPid;
    private String pddUnion;

    private String pddCustomerParameters;
    private String dyChannel;

}
