/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwUserBillQueryCriteria {
    @Query(type = Query.Type.EQUAL)
    private String category = "";
    @Query(type = Query.Type.EQUAL)
    private String type  = "";
    @Query(type = Query.Type.EQUAL)
    private Integer pm;
    @Query(blurry = "mark")
    private String mark = "";

    @Query(type = Query.Type.EQUAL)
    private Long origUid;

    @Query(type = Query.Type.EQUAL)
    private Long uid;
    private String startTime;

    private String endTime;

    @Query(type = Query.Type.EQUAL)
    private Integer unlockStatus;
}
