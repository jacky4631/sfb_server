/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.order.domain;

import lombok.Data;

/**
* @author shenji
* @date 2022-08-29
*/
@Data
public class TkOrderParam{

    /** 订单号 */
    private String orderId;

    /** 用户id */
    private Long uid;

}
