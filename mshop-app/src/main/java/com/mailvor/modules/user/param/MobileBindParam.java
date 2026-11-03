package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @author : gzlv 2021/7/20 15:09
 */
@Data
public class MobileBindParam {

    @NotBlank(message = "手机号必填")
    @Schema(description = "手机号码")
    private String account;

    @NotBlank(message = "验证码必填")
    @Schema(description = "验证码")
    private String captcha;

}
