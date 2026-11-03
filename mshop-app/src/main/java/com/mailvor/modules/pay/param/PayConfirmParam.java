package com.mailvor.modules.pay.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class PayConfirmParam implements Serializable {

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;
    @NotBlank(message = "网关流水号不能为空")
    @Schema(description = "网关流水号")
    private String bizSn;
    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderId;


}
