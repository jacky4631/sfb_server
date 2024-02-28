package com.mailvor.modules.pay.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class PayBankBindParam implements Serializable {

    @NotBlank(message = "银行卡号不能为空")
    @ApiModelProperty(value = "银行卡号")
    private String bankNo;
    @NotNull(message = "支付类型不能为空")
    @ApiModelProperty(value = "支付类型 1=支付宝 2=微信 3=银行卡 4=银行卡绑卡 其他未知")
    private Integer payType;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号")
    private String phone;

}
