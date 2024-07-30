package com.mailvor.modules.pay.adapay;

import lombok.Data;

@Data
public class AdaPayConfig {
	private String appId = "";
	private String payChannel = "alipay";

	private String apiKey;

	private String mockApiKey;
	private String rsaPrivateKey;
	private String merchantKey;
	/**
	 * debug 模式，开启后与详细的日志
	 */
	private Boolean debug = true;

	/**
	 * prodMode 模式，默认为生产模式，false可以使用mock模式
	 */
	private Boolean prodMode = true;
}
