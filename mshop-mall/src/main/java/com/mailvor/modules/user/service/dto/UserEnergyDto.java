/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UserMoneyDTO
 * @author huangyu
 * @Date 2019/12/4
 **/
@Data
public class UserEnergyDto implements Serializable {
    @NotNull(message = "参数缺失")
    private Long uid;
    @NotNull(message = "请选择修改热度方式")
    private Integer ptype;
   //@NotNull(message = "金额必填")
    @Min(message = "最低金额为0",value = 0)
    private Double energy;

    @NotBlank
    private String platform;
    @NotNull
    private Integer type;
    @NotBlank
    private String opePwd;
}
