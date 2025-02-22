/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author huangyu
 * @Date 2019/11/13
 **/
@Data
public class UserExtParam implements Serializable {

    @ApiModelProperty(value = "提现支付宝用户名")
    private String alipayCode;

    @NotBlank(message = "提现类型不能为空")
    @ApiModelProperty(value = "提现类型 weixin alipay bank")
    private String extractType;


    @ApiModelProperty(value = "extractType=bank时必传")
    private Long bankId;

    @NotBlank(message = "金额不能为空")
    @ApiModelProperty(value = "提现金额")
    private String money;

    @ApiModelProperty(value = "微信号")
    private String weixin;

    @ApiModelProperty(value = "支付宝账号")
    private String name;

    /**
     * mark=jkj 说明是九块九购物
     * */
    private String mark;
}
