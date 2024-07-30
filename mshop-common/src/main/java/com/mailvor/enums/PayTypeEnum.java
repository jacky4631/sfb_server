/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author huangyu
 * 支付相关枚举
 */
@Getter
@AllArgsConstructor
public enum PayTypeEnum {

	ALI("alipay","支付宝支付", 1),
	ALI_OTHER("alipay","支付宝-三方支付", 2),
	WEIXIN("weixin","微信支付", 6),
	WEIXIN_OTHER("weixin","微信支付-三方", 7),
	BANK("bank","银行卡快捷", 12),
	BANK_BIND("bankBind","银行卡绑卡", 13),
	IOS("ios","IOS支付", 0),
	YUE("yue","余额支付", 3),
	INTEGRAL("integral","积分兑换", 4),

	UNIONPAY("unionPay","云闪付", 5);


	private String value;
	private String desc;
	private Integer type;

	public static PayTypeEnum toType(String value) {
		return Stream.of(PayTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
