package com.mailvor.modules.user.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class BankExtractParam implements Serializable {
    @NotBlank
    private String opePwd;
    @NotNull(message = "提现金额不能为空")
    @ApiModelProperty(value = "提现金额")
    private BigDecimal price;


}
