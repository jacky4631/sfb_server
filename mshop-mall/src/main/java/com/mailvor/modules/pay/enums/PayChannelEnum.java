/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author huangyu
 * 账单明细相关枚举
 */
@Getter
@AllArgsConstructor
public enum PayChannelEnum {

	//这里的key如果没有后缀的都是支付宝支付
	iOSPAY("iospay","苹果支付"),
	ADAPAY("adapay","汇付天下"),
	ALLINPAY("allinpay","通联支付收银宝"),
	ALIPAY("alipay","支付宝原生"),
	ALIPAY_WEB("alipayweb","支付宝网页"),
	WECHATPAY("wechatpay","微信原生"),
	YEEPAY("yeepay","易宝支付"),
	YEEPAY_BANK("yeepay_bank","易宝快捷支付"),
	ADAPAY_BANK("adapay_bank","易宝支付"),
	YSEPAY("ysepay","银盛支付"),
	YSEPAY_BANK_BIND("ysepay_bank_bind","银盛绑卡支付");

	private String key;
	private String name;

	public static PayChannelEnum toKey(String key) {
		return Stream.of(PayChannelEnum.values())
				.filter(p -> p.key.equals(key))
				.findAny()
				.orElse(null);
	}
}
