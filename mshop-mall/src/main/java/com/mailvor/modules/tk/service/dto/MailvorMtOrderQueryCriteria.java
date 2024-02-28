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
* @author shenji
* @date 2022-08-29
*/
@Data
public class MailvorMtOrderQueryCriteria extends MailvorOrderQueryCriteria{
    @Query(type = Query.Type.EQUAL)
    private Integer itemStatus;

    @Query(type = Query.Type.EQUAL)
    private Integer itemBizStatus;

    @Query(blurry = "uniqueItemId,orderId,uid")
    private String value;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> orderPayTime;
}
