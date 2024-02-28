/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @author huangyu
* @date 2020-06-29
*/
@Data
public class MwShippingTemplatesFreeDto implements Serializable {

    /** 编号 */
    private Integer id;

    /** 省ID */
    private Integer provinceId;

    /** 模板ID */
    private Integer tempId;

    /** 城市ID */
    private Integer cityId;

    /** 包邮件数 */
    private BigDecimal number;

    /** 包邮金额 */
    private BigDecimal price;

    /** 计费方式 */
    private Integer type;

    /** 分组唯一值 */
    private String uniqid;
}
