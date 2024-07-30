/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.category.service.dto;

import lombok.Data;

import java.io.Serializable;


/**
* @author mazhongjun
* @date 2019-10-03
*/
@Data
public class MwStoreCategorySmallDto implements Serializable {

    // 商品分类表ID
    private Integer id;


    // 分类名称
    private String cateName;



}
