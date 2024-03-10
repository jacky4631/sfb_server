package com.mailvor.modules.pay.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class PayChannelParam implements Serializable {

    @NotBlank(message = "充值参数有误")
    @ApiModelProperty(value = "用户充值ID")
    private String rechargeId;

    @ApiModelProperty(value = "来源")
    private String from;

    //@NotNull(message = "金额必填")
   // @Min(value = 1,message = "充值金额不能低于1")
    @ApiModelProperty(value = "充值金额")
    private Double price;

    @ApiModelProperty(value = "充值单号")
    private String orderSn;

    @NotBlank(message = "平台不能为空")
    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;

    @NotNull(message = "支付类型不能为空")
    @ApiModelProperty(value = "支付类型 0=ios支付 1=支付宝 2=微信 3=银行卡 4=银行卡绑卡 其他未知")
    private Integer payType;

    @ApiModelProperty(value = "充值用户UID")
    private Long uid;

    @ApiModelProperty(value = "用户银行卡id payType=4是必须传入")
    private Long bankId;

    @ApiModelProperty(value = "订单类型 0=加盟 2=月卡")
    private Integer type;


}
