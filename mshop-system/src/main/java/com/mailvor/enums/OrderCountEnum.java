/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author mazhongjun
 * 订单相关枚举
 */
@Getter
@AllArgsConstructor
public enum OrderCountEnum {

	TODAY(1,"今天"),
	YESTERDAY(2,"昨天"),
	WEEK(3,"上周"),
	MONTH(4,"本月");



	private Integer value;
	private String desc;

	public static OrderCountEnum toType(int value) {
		return Stream.of(OrderCountEnum.values())
				.filter(p -> p.value == value)
				.findAny()
				.orElse(null);
	}


}
