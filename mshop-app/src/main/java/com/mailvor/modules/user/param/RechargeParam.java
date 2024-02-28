package com.mailvor.modules.user.param;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RechargeParam  implements Serializable {

    @NotBlank(message = "充值参数有误")
    @JsonProperty(value = "rechar_id")
    @ApiModelProperty(value = "用户充值ID")
    private String recharId;

    @ApiModelProperty(value = "来源")
    private String from;

    //@NotNull(message = "金额必填")
   // @Min(value = 1,message = "充值金额不能低于1")
    @ApiModelProperty(value = "充值金额")
    private Double price;

    @ApiModelProperty(value = "充值单号")
    private String orderSn;

    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;
}
