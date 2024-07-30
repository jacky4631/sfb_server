package com.mailvor.modules.pay.ysepay;

import lombok.Data;

@Data
public class YsePayConfig {
	private String merchantNo;

	private String prxPath;
	private String prxPwd;

	private String desKey;
}
