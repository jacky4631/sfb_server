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
public enum PayMethodEnum {

	WECHAT("wechat","公众号支付"),
	WXAPP("wxapp","小程序支付"),
	APP("app","app支付");


	private String value;
	private String desc;

	public static PayMethodEnum toType(String value) {
		return Stream.of(PayMethodEnum.values())
				.filter(p -> p.value.equals(value))
				.findAny()
				.orElse(null);
	}


}
