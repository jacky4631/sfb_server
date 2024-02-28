package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class RechargeCouponParam implements Serializable {

    @NotBlank(message = "充值参数有误")
    @ApiModelProperty(value = "用户充值ID")
    private String rechargeId;
    @NotBlank(message = "平台参数有误")
    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;
}
