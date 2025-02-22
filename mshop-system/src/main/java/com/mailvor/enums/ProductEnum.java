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
 * 产品相关枚举
 */
@Getter
@AllArgsConstructor
public enum ProductEnum {

	TYPE_1(1,"精品推荐"),
	TYPE_2(2,"热门榜单"),
	TYPE_3(3,"首发新品"),
	TYPE_4(4,"猜你喜欢");


	private Integer value;
	private String desc;

	public static ProductEnum toType(int value) {
		return Stream.of(ProductEnum.values())
				.filter(p -> p.value == value)
				.findAny()
				.orElse(null);
	}


}
