/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName UserMoneyDTO
 * @author huangyu
 * @Date 2019/12/4
 **/
@Data
public class UserIntegralDto implements Serializable {
   //@NotNull(message = "参数缺失")
    private Long uid;
   //@NotNull(message = "请选择修改余额方式")
    private Integer ptype;
   //@NotNull(message = "金额必填")
    @Min(message = "最低金额为0",value = 0)
    private Double integral;

    @NotBlank
    private String opePwd;
}
