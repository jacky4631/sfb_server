/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import com.mailvor.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangyu
 * 账单明细相关枚举
 */
@Getter
@AllArgsConstructor
public enum PlatformEnum {

	TB("tb","淘宝"),
	JD("jd","京东"),
	PDD("pdd","拼多多"),
	DY("dy","抖音"),
	VIP("vip","唯品会"),
	MT("mt","美团");



	private String value;
	private String desc;


	public static PlatformEnum get(String value) {
		if(StringUtils.isBlank(value)) {
			return null;
		}
		for(PlatformEnum platformEnum : values()) {
			if(platformEnum.getValue().equals(value)) {
				return platformEnum;
			}
		}
		return null;
	}
}
