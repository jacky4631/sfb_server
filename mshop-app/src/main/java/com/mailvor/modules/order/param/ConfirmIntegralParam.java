package com.mailvor.modules.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName 确认订单ConfirmOrderDTO
 * @author huangyu
 * @Date 2020/6/21
 **/
@Getter
@Setter
public class ConfirmIntegralParam {

    @ApiModelProperty(value = "商品ID")
    private Long id;
}
