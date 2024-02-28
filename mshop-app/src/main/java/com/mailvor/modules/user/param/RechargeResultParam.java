package com.mailvor.modules.user.param;

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
public class RechargeResultParam implements Serializable {

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderId;
    @NotBlank(message = "充值结果不能为空")
    @ApiModelProperty(value = "充值结果")
    private String result;
}
