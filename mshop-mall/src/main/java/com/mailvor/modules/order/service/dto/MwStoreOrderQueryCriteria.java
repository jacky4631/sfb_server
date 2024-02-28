/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.dto;

import com.mailvor.annotation.Query;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwStoreOrderQueryCriteria {

    // 模糊
    @Query(type = Query.Type.UNIX_TIMESTAMP)
    private List<String> createTime;


    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String orderId;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String realName;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String userPhone;

    @Query
    private Integer paid;

    @Query
    private Integer status;

    @Query
    private Integer refundStatus;

    @Query
    private Integer isDel;

    @Query
    private Integer combinationId;

    @Query
    private Integer seckillId;

    @Query
    private Integer bargainId;

    @Query(propName="combinationId",type = Query.Type.NOT_EQUAL)
    private Integer newCombinationId;

    @Query(propName="seckillId",type = Query.Type.NOT_EQUAL)
    private Integer newSeckillId;

    @Query(propName="bargainId",type = Query.Type.NOT_EQUAL)
    private Integer newBargainId;

    @Query(type = Query.Type.GREATER_THAN_NQ)
    private BigDecimal payIntegral;

    @Query
    private Integer storeId;
}
