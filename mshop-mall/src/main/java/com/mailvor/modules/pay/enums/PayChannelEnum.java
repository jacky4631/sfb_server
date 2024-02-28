/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.mailvor.modules.utils.PayUtil.*;

/**
 * @author huangyu
 * 账单明细相关枚举
 */
@Getter
@AllArgsConstructor
public enum PayChannelEnum {

	adapay(CHANNEL_KEY_ADAPAY,"汇付天下"),
	allinpay(CHANNEL_KEY_ALLINPAY,"通联支付收银宝"),
	alipay(CHANNEL_KEY_ALIPAY,"支付宝原生"),
	alipayweb(CHANNEL_KEY_ALIPAY_WEB,"支付宝网页"),
	wechatpay(CHANNEL_KEY_WECHATPAY,"微信原生"),
	yeepay(CHANNEL_KEY_YEEPAY,"易宝支付");

	private String key;
	private String name;


}
