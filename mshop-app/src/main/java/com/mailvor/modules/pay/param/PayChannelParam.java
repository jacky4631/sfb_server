package com.mailvor.modules.pay.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class PayChannelParam implements Serializable {

    @NotBlank(message = "充值参数有误")
    @Schema(description = "用户充值ID")
    private String rechargeId;

    @Schema(description = "来源")
    private String from;

    //@NotNull(message = "金额必填")
   // @Min(value = 1,message = "充值金额不能低于1")
    @Schema(description = "充值金额")
    private Double price;

    @Schema(description = "充值单号")
    private String orderSn;

    @NotBlank(message = "平台不能为空")
    @Schema(description = "平台 tb jd pdd dy vip")
    private String platform;

    @NotNull(message = "支付类型不能为空")
    @Schema(description = "支付类型 0=ios支付 1=支付宝 2=微信 3=银行卡 4=银行卡绑卡 其他未知")
    private Integer payType;

    @Schema(description = "充值用户UID")
    private Long uid;

    @Schema(description = "用户银行卡id payType=4是必须传入")
    private Long bankId;

    @Schema(description = "订单类型 0=加盟 2=月卡")
    private Integer type;


}
