/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class MwUserExtractQueryCriteria {

    @Query(type = Query.Type.EQUAL)
    private Integer status;
    @Query(type = Query.Type.EQUAL)
    private Long uid;
    @Query(blurry = "realName,bankCode,alipayCode,uid")
    private String value;


}
