package com.mailvor.modules.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * @ClassName 确认订单ConfirmOrderDTO
 * @author huangyu
 * @Date 2020/6/21
 **/
@Getter
@Setter
public class ConfirmOrderParam {

    @NotBlank(message = "请提交购买的商品")
    @Schema(description = "购物车ID")
    private Long goodsId;
}
