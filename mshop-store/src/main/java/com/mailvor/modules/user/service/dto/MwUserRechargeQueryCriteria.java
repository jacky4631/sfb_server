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
public class MwUserRechargeQueryCriteria {

    /** 模糊 */
    @Query(type = Query.Type.EQUAL)
    private Long uid;
    @Query(blurry = "nickname,order_id")
    private String blurry;

    @Query(type = Query.Type.EQUAL)
    private Integer paid;

    @Query(type = Query.Type.EQUAL)
    private Integer type;
}
