package com.mailvor.modules.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName 确认订单ConfirmOrderDTO
 * @Date 2020/6/21
 **/
@Getter
@Setter
public class SubmitOrderParam {

    @NotBlank(message = "订单id不能为空")
    @ApiModelProperty(value = "订单id")
    private String orderId;
}
