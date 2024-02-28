/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dict.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-14
*/
@Data
public class DictDto implements Serializable {

    /** 字典ID */
    private Long id;

    /** 字典名称 */
    private String name;

    private List<DictDetailDto> dictDetails;

    /** 描述 */
    private String remark;

    /** 创建日期 */
    private Timestamp createTime;
}
