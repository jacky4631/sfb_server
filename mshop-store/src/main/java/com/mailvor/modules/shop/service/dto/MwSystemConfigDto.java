/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwSystemConfigDto implements Serializable {

    /** 配置id */
    private Integer id;

    /** 字段名称 */
    private String menuName;

    /** 默认值 */
    private String value;

    /** 排序 */
    private Integer sort;

    /** 是否隐藏 */
    private Integer status;
}
