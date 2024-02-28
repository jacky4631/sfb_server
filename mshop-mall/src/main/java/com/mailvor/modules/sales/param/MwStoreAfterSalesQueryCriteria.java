/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.sales.param;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
* @author gzlv
* @date 2021-06-30
*/
@Data
public class MwStoreAfterSalesQueryCriteria {

    /**
     * 订单号
     */
    private String orderCode;

    /** 售后状态-0正常1用户取消2商家拒绝 */
    private Integer salesState;

    private List<Timestamp> time;

    /** 状态 0已提交等待平台审核 1平台已审核 等待用户发货/退款 2 用户已发货 3已完成 */
    private Integer state;

    /** 服务类型0仅退款1退货退款 */
    private Integer serviceType;
}
