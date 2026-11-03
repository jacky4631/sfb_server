package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class RechargeParam  implements Serializable {

    @NotBlank(message = "充值参数有误")
    @JsonProperty(value = "rechar_id")
    @Schema(description = "用户充值ID")
    private String recharId;

    @Schema(description = "来源")
    private String from;

    //@NotNull(message = "金额必填")
   // @Min(value = 1,message = "充值金额不能低于1")
    @Schema(description = "充值金额")
    private Double price;

    @Schema(description = "充值单号")
    private String orderSn;

    @Schema(description = "平台 tb jd pdd dy vip")
    private String platform;
}
