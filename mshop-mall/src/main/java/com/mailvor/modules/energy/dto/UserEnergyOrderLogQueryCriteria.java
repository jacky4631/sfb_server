/**
 * Copyright (C) 2018-2024
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
public class UserEnergyOrderLogQueryCriteria {
    @Query(blurry = "uid,platform")
    private String search;
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

    @Query(type = Query.Type.EQUAL)
    private Integer type;
}
