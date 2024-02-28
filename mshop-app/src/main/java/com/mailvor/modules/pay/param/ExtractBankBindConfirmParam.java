package com.mailvor.modules.pay.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class ExtractBankBindConfirmParam implements Serializable {

    @NotBlank(message = "业务请求号不能为空")
    @ApiModelProperty(value = "业务请求号")
    private String requestNo;
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码")
    private String code;
    @NotBlank(message = "认证序列号不能为空")
    @ApiModelProperty(value = "认证序列号")
    private String authSn;
}
