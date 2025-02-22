package com.mailvor.modules.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName 确认订单ConfirmOrderDTO
 * @author huangyu
 * @Date 2020/6/21
 **/
@Getter
@Setter
public class ConfirmOrderParam {

    @NotBlank(message = "请提交购买的商品")
    @ApiModelProperty(value = "购物车ID")
    private Long goodsId;
}
