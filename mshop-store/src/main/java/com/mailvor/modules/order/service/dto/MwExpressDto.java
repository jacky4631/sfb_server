/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwExpressDto implements Serializable {

    /** 快递公司id */
    private Integer id;

    /** 快递公司简称 */
    private String code;

    /** 快递公司全称 */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 是否显示 */
    private Integer isShow;
}
