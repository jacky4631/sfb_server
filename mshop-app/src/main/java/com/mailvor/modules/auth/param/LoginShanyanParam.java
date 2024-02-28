package com.mailvor.modules.auth.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : gzlv 2023/03/09 15:09
 */
@Data
public class LoginShanyanParam {

    @NotBlank(message = "token必填")
    @ApiModelProperty(value = "token")
    private String token;

    @NotNull(message = "平台类型必填")
    @ApiModelProperty(value = "平台类型 1=android 2=ios")
    private Integer type;
}
