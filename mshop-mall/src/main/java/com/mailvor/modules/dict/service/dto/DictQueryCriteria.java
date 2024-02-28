/**
 * Copyright (C) 2018-2022
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
public class DictQueryCriteria{

    @Query(blurry = "name,remark")
    private String blurry;
}
