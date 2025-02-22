/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangyu
 * 应用来源相关枚举
 */
@Getter
@AllArgsConstructor
public enum AppFromEnum {

	WEIXIN_H5("weixinh5","weixinh5"),
	H5("h5","H5"),
	WECHAT("wechat","公众号"),
	APP("app","APP"),
	PC("pc","PC"),
	ROUNTINE("routine","小程序"),
	UNIAPPH5("uniappH5","uniappH5");


	private String value;
	private String desc;


}
