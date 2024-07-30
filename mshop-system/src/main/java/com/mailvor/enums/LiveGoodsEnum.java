/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangyu
 * 直播商品相关枚举
 */
@Getter
@AllArgsConstructor
public enum LiveGoodsEnum {

	IS_Audit_0(0,"未审核"),
	IS_Audit_1(1,"审核中"),
	IS_Audit_2(2,"审核通过"),
	IS_Audit_3(3,"审核失败");

	private Integer value;
	private String desc;

}
