/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
* @author wangjun
* @date 2022-09-07
*/
@Data
public class MailvorVipOrderQueryCriteria extends MailvorOrderQueryCriteria{
    @Query
    private String orderSubStatusName;

    @Query(blurry = "orderSn,detailList,uid")
    private String value;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> orderTime;
}
