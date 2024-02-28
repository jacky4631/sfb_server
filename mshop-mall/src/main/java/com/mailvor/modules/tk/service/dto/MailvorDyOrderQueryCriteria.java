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
* @date 2022-09-07
*/
@Data
public class MailvorDyOrderQueryCriteria extends MailvorOrderQueryCriteria{
    @Query
    private String orderId;

    @Query(blurry = "productName,orderId,uid")
    private String value;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> paySuccessTime;
}
