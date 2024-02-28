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
public class PayConfirmParam implements Serializable {

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码")
    private String code;
    @NotBlank(message = "网关流水号不能为空")
    @ApiModelProperty(value = "网关流水号")
    private String bizSn;
    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderId;


}
