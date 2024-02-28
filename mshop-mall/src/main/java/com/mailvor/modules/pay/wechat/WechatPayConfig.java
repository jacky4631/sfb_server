package com.mailvor.modules.pay.wechat;

import lombok.Data;

@Data
public class WechatPayConfig {
	private String appId;
	private String mchId;
	private String mchKey;

	private String keyPath;
}
