/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwMaterialGroupDto implements Serializable {
    /** PK */
    private String id;


    /** 创建时间 */
    private Date createTime;

    /** 创建者ID */
    private String createId;

    /** 分组名 */
    private String name;
}
