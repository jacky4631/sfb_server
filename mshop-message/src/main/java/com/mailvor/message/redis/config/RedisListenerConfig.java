/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.message.redis.config;


import cn.hutool.core.util.StrUtil;
import com.mailvor.message.redis.listener.RedisKeyExpirationListener;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.order.service.MwStoreOrderService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis监听配置
 * @author huangyu
 * @since 2020-02-27
 */

@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
public class RedisListenerConfig {

	private final RedisTemplate<String, String> redisTemplate;
	private final RedisConfigProperties redisConfigProperties;
	private final MwStoreOrderService storeOrderService;
	private final MwStorePinkService storePinkService;

	@Bean
    RedisMessageListenerContainer container(RedisConnectionFactory factory) {
		String topic =StrUtil.format("__keyevent@{}__:expired", redisConfigProperties.getDatabase());
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(factory);
		container.addMessageListener(new RedisKeyExpirationListener(redisTemplate,redisConfigProperties
		 ,storeOrderService,storePinkService), new PatternTopic(topic));
		return container;
	}
}

