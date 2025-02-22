/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 售后状态枚举
 *
 * @author huangyu
 * @date 2021/12/21
 */
@Getter
@AllArgsConstructor
public enum AfterSalesStatusEnum {

	STATUS_0(0,"已提交等待平台审核"),
	STATUS_1(1,"平台已审核,等待用户发货/退款"),
	STATUS_2(2,"用户已发货"),
	STATUS_3(3,"已完成");

	private Integer value;
	private String desc;

	public static AfterSalesStatusEnum toType(int value) {
		return Stream.of(AfterSalesStatusEnum.values())
				.filter(p -> p.value == value)
				.findAny()
				.orElse(null);
	}


}
