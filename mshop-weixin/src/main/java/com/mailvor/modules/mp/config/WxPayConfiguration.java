/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.config;

import com.mailvor.enums.PayMethodEnum;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.ShopKeyUtils;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.modules.tools.config.WechatConfig.*;

/**
 * 支付配置
 * @author huangyu
 * @date 2020/03/01
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WxPayConfiguration {

	private static Map<String, WxPayService> payServices = Maps.newHashMap();

	private static RedisUtils redisUtils;

	@Autowired
	public WxPayConfiguration(RedisUtils redisUtils) {
		WxPayConfiguration.redisUtils = redisUtils;
	}

	/**
	 *  获取WxPayService
	 * @return
	 */
	public static WxPayService getPayService(PayMethodEnum payMethodEnum) {
		WxPayService wxPayService = payServices.get(ShopKeyUtils.getMshopWeiXinPayService(PAY_NAME)+payMethodEnum.getValue());
		if(wxPayService == null || redisUtils.get(ShopKeyUtils.getMshopWeiXinPayService(PAY_NAME)) == null) {
			WxPayConfig payConfig = new WxPayConfig();
			switch (payMethodEnum){
				case WECHAT:
					payConfig.setAppId(redisUtils.getY(ShopKeyUtils.getWechatAppId()));
					break;
				case WXAPP:
					payConfig.setAppId(RedisUtil.get(ShopKeyUtils.getWxAppAppId()));
					break;
				case APP:
					payConfig.setAppId(APP_ID);
					break;
				default:
			}

			payConfig.setMchId(MCH_ID);
			payConfig.setMchKey(MCH_KEY);
			payConfig.setKeyPath(MCH_CERT_PATH);
			// 可以指定是否使用沙箱环境
			payConfig.setUseSandboxEnv(false);
			wxPayService = new WxPayServiceImpl();
			wxPayService.setConfig(payConfig);
			payServices.put(ShopKeyUtils.getMshopWeiXinPayService(PAY_NAME)+payMethodEnum.getValue(), wxPayService);

			//增加标识
			redisUtils.set(ShopKeyUtils.getMshopWeiXinPayService(PAY_NAME),PAY_NAME);
		}
		return wxPayService;
    }
	/**
	 * 移除WxPayService
	 */
	public static void removeWxPayService(){
		redisUtils.del(ShopKeyUtils.getMshopWeiXinPayService(PAY_NAME));
		payServices.remove(ShopKeyUtils.getMshopWeiXinPayService(PAY_NAME));
		//payServices.remove(ShopKeyUtils.getMshopWeiXinMiniPayService());
		//payServices.remove(ShopKeyUtils.getMshopWeiXinAppPayService());
	}

}
