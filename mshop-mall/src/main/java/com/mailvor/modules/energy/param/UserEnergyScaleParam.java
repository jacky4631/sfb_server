package com.mailvor.modules.energy.param;


import com.mailvor.domain.BaseDomain;
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
public class UserEnergyScaleParam extends BaseDomain {

    private static final long serialVersionUID = 1L;
    /** 用户id */

    @NotNull
    private Long uid;
    @NotNull
    /** 体验翻倍比例 */
    private BigDecimal expScale;
    @NotNull
    /** 月卡翻倍比例 */
    private BigDecimal monthScale;
    /** 体验翻倍失效天数，相比create_time */
    @NotNull
    private Integer expInvalidDay;
    /** 月卡翻倍失效天数，相比create_time */
    @NotNull
    private Integer monthInvalidDay;
    @NotBlank
    private String opePwd;

}
