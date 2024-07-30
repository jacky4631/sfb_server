/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
* @author shenji
* @date 2022-09-05
*/
@Data
public class MailvorJdOrderQueryCriteria extends MailvorOrderQueryCriteria{
    @Query
    private Integer validCode;

    @Query(blurry = "skuName,id,orderId,parentId,uid")
    private String value;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> orderTime;
}
