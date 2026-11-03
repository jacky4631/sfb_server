package com.mailvor.modules.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * @ClassName ProductOrderParam
 * @author huangyu
 * @Date 2020/6/23
 **/
@Getter
@Setter
public class ProductOrderParam {

    @NotBlank(message = "参数有误")
    @Schema(description = "订单唯一值")
    private String unique;
}
