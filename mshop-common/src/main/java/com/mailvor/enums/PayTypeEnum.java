/**
 * Copyright (C) 2018-2022
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

	ALI("alipay","支付宝支付"),
	WEIXIN("weixin","微信支付"),
	BANK("bank","银行卡快捷"),
	BANK_BIND("bankBind","银行卡绑卡"),
	IOS("ios","IOS支付"),
	YUE("yue","余额支付"),
	INTEGRAL("integral","积分兑换"),

	UNIONPAY("unionPay","云闪付");


	private String value;
	private String desc;

	public static PayTypeEnum toType(String value) {
		return Stream.of(PayTypeEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
