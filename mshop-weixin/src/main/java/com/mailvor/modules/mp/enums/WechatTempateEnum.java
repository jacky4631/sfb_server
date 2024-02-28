/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangyu
 * 微信公众号模板枚举
 */
@Getter
@AllArgsConstructor
public enum WechatTempateEnum {
    PAY_SUCCESS("pay_success","支付成功通知"),
    DELIVERY_SUCCESS("delivery_success","发货成功通知"),
    REFUND_SUCCESS("refund_success","退款成功通知"),
    RECHARGE_SUCCESS("recharge_success","充值成功通知");

    private String value; //模板编号
    private String desc; //模板id
}
