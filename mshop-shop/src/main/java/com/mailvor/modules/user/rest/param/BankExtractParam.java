package com.mailvor.modules.user.rest.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class BankExtractParam implements Serializable {
    @NotNull(message = "提现金额不能为空")
    @Schema(description = "提现金额")
    private BigDecimal price;


}
