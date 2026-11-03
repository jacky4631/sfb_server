package com.mailvor.modules.pay.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class PayBankBindConfirmParam implements Serializable {

    @NotBlank(message = "业务请求号不能为空")
    @Schema(description = "业务请求号")
    private String requestNo;
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;

    @NotNull(message = "支付类型不能为空")
    @Schema(description = "支付类型 1=支付宝 2=微信 3=银行卡 4=银行卡绑卡 其他未知")
    private Integer payType;
}
