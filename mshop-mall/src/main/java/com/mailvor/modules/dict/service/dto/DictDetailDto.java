/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dict.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @author huangyu
* @date 2020-05-14
*/
@Data
public class DictDetailDto implements Serializable {

    /** 字典详细 */
    private Long id;

    /** 字典标签 */
    private String label;

    /** 字典值 */
    private String value;

    /** 排序 */
    private String sort;

    /** 字典id */
    private Long dictId;

    /** 创建日期 */
    private Timestamp createTime;
}
