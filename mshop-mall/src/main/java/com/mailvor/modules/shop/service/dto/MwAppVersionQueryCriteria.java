/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author lioncity
* @date 2020-12-09
*/
@Data
public class MwAppVersionQueryCriteria {
    @Query(type = Query.Type.EQUAL)
    private String platformName;

}
