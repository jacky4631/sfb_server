package com.mailvor.modules.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "商品ID")
    private Long id;
}
