/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwStoreOrderCartInfoDto implements Serializable {

    private Integer id;

    /** 订单id */
    private Integer oid;

    /**
     * 订单号
     */
    private String orderId;

    /** 购物车id */
    private Integer cartId;

    /** 商品ID */
    private Integer productId;

    /** 购买东西的详细信息 */
    private String cartInfo;

    /** 唯一id */
    private String unique;
}
