/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
* @author huangyu
* @date 2023-02-09
*/
@Data
public class UserEnergyQueryCriteria {

    @Query(type = Query.Type.EQUAL)
    private Long uid;


}
