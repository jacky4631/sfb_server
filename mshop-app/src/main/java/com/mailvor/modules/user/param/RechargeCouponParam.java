package com.mailvor.modules.user.param;

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
public class RechargeCouponParam implements Serializable {

    @NotBlank(message = "充值参数有误")
    @Schema(description = "用户充值ID")
    private String rechargeId;
    @NotBlank(message = "平台参数有误")
    @Schema(description = "平台 tb jd pdd dy vip")
    private String platform;
}
