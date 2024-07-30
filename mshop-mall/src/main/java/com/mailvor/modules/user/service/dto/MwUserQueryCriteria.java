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
public class MwUserQueryCriteria {

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String nickname;

    @Query(blurry = "nickname,phone,uid,code,additional_no,real_name")
    private String blurry;
    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String phone;

    @Query
    private String userType;

    @Query(type = Query.Type.EQUAL)
    private Long uid;

    private Integer grade;
}
