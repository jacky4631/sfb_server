/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
* @author shenji
* @date 2022-08-29
*/
@Data
public class MailvorTbOrderQueryCriteria extends MailvorOrderQueryCriteria{
    @Query
    private Integer tkStatus;

    @Query(blurry = "itemTitle,tradeParentId,tradeId,uid")
    private String value;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> tkCreateTime;
}
