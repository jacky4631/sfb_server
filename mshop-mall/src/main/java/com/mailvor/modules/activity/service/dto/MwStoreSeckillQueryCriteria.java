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
public class MwStoreSeckillQueryCriteria {


    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String title;
}
