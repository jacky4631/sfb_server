package com.mailvor.modules.auth.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author : gzlv 2023/03/09 15:09
 */
@Data
public class LoginShanyanParam {

    @NotBlank(message = "token必填")
    @Schema(description = "token")
    private String token;

    @NotNull(message = "平台类型必填")
    @Schema(description = "平台类型 1=android 2=ios")
    private Integer type;
}
