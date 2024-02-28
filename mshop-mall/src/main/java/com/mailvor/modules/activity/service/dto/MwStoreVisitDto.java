/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class MwStoreVisitDto implements Serializable {

    private Integer id;

    /** 产品ID */
    private Integer productId;

    /** 产品类型 */
    private String productType;

    /** 产品分类ID */
    private Integer cateId;

    /** 产品类型 */
    private String type;

    /** 用户ID */
    private Integer uid;

    /** 访问次数 */
    private Integer count;

    /** 备注描述 */
    private String content;

    /** 添加时间 */
    private Integer addTime;
}
