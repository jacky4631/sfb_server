/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.rest.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwExpendParam implements Serializable {

    @NotNull(message = "支出金额不能为空")
    @DecimalMin(value = "0.01", message = "支出金额不能小于0.01元")
    private BigDecimal money;

    @NotBlank(message = "备注不能为空")
    private String mark;

    /**
     * 支出日期
     * */
    @NotNull(message = "支出时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date addTime;

}
