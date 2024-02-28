package com.mailvor.modules.energy.param;


import com.mailvor.domain.BaseDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * <p>
 * 用户流量扶持
 * </p>
 *
 * @author huangyu
 * @since 2023-2-4
 */
@Data
public class UserEnergyParam extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "用户流量扶持id")
    private Long id;


    @NotNull
    @ApiModelProperty(value = "拆红包金额")
    private BigDecimal releaseMoney;

    @NotBlank
    private String opePwd;

}
