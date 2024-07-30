/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.dto;

import lombok.Data;
import com.mailvor.annotation.Query;

/**
 * @author huangyu
 * @date 2020-09-03
 */
@Data
public class MwStoreProductRelationQueryCriteria {
    @Query(type = Query.Type.EQUAL)
    private String type;
}
