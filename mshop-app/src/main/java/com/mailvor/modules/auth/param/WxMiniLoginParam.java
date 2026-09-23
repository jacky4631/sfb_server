package com.mailvor.modules.auth.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 小程序登录参数
 */
@Data
public class WxMiniLoginParam {

    @NotBlank(message = "code必填")
    @ApiModelProperty(value = "小程序 wx.login 返回的 code")
    private String code;

}
