package com.mailvor.modules.pay.wechat;

import lombok.Data;

@Data
public class WechatPayConfig {
	private String appId;
	private String mchId;
	private String mchKey;

	private String keyPath;
	/** PKCS12 证书的 Base64 内容。 */
	private String keyContent;
}
