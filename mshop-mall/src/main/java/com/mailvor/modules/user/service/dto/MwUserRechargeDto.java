/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/
@Data
public class MwUserRechargeDto implements Serializable {

    private Long id;

    /** 充值用户UID */
    private Long uid;
    private Long oid;
    /** 订单号 */
    private String orderId;

    /** 充值金额 */
    private BigDecimal price;

    /** 充值类型 */
    private String rechargeType;

    /** 是否充值 */
    private Integer paid;

    /** 充值支付时间 */
    private Date payTime;

    /** 充值时间 */
    private Date createTime;

    /** 通道id */
    private Long channelId;
    /** 退款金额 */
    private BigDecimal refundPrice;

    /** 昵称 */
    private String nickname;

    private String platform;

    private String result;
    private Integer type;
}
