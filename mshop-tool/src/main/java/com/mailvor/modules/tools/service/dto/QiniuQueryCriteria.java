/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author huangyu
 * @date 2019-6-4 09:54:37
 */
@Data
public class QiniuQueryCriteria{

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
