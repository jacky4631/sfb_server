/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.dict.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author huangyu
* @date 2020-05-14
*/
@Data
public class DictDetailQueryCriteria{

    @Query(type = Query.Type.INNER_LIKE)
    private String label;

    private String dictName;
}
