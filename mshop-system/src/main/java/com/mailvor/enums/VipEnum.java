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
public enum VipEnum {

	VIP(5,"会员"),
	EXPIRED(99,"不是会员");

	private Integer level;
	private String name;


	public static VipEnum get(String value) {
		if(StringUtils.isBlank(value)) {
			return null;
		}
		for(VipEnum vipEnum : values()) {
			if(vipEnum.getLevel().equals(value)) {
				return vipEnum;
			}
		}
		return null;
	}
}
