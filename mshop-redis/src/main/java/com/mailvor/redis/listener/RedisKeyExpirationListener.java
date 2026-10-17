/**
 * Copyright (C) 2018-2026
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.redis.listener;


import cn.hutool.core.util.StrUtil;
import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.redis.config.RedisConfigProperties;
import com.mailvor.modules.activity.domain.MwStorePink;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.order.domain.MwStoreOrder;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * redis过期监听
 * @author huangyu
 * @since 2020-02-27
 */
@Component
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

	private RedisTemplate<String, String> redisTemplate;
	private RedisConfigProperties redisConfigProperties;
	private MwStoreOrderService storeOrderService;
	private MwStorePinkService storePinkService;

	public RedisKeyExpirationListener(RedisTemplate<String, String> redisTemplate,
                                      RedisConfigProperties redisConfigProperties,
									  MwStoreOrderService storeOrderService,
									  MwStorePinkService storePinkService){
		this.redisTemplate = redisTemplate;
		this.redisConfigProperties = redisConfigProperties;
		this.storeOrderService = storeOrderService;
		this.storePinkService = storePinkService;
	}
	@Override
	public void onMessage(Message message, byte[] bytes) {
 		RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
		String channel = String.valueOf(serializer.deserialize(message.getChannel()));
		String body = String.valueOf(serializer.deserialize(message.getBody()));
		log.debug("redis key过期：{}", body);
		//key过期监听
		if(StrUtil.format("__keyevent@{}__:expired", redisConfigProperties.getDatabase()).equals(channel)){
			//订单自动取消
			if(body.contains(ShopConstants.REDIS_ORDER_OUTTIME_UNPAY)) {
				body = body.replace(ShopConstants.REDIS_ORDER_OUTTIME_UNPAY, "");
				log.info("body:{}",body);
				String orderId = body;
				MwStoreOrder order = storeOrderService.getOne(new LambdaQueryWrapper<MwStoreOrder>()
						.eq(MwStoreOrder::getId, orderId));
				//只有待支付的订单能取消
				if(order != null){
					storeOrderService.cancelOrder(order.getOrderId(),null);
					log.info("订单id:{},未在规定时间支付取消成功",body);
				}
			}
			//订单自动收货
			if(body.contains(ShopConstants.REDIS_ORDER_OUTTIME_UNCONFIRM)) {
				body = body.replace(ShopConstants.REDIS_ORDER_OUTTIME_UNCONFIRM, "");
				log.info("body:{}",body);
				String orderId = body;
				MwStoreOrder order = storeOrderService.getOne(new LambdaQueryWrapper<MwStoreOrder>()
                        .eq(MwStoreOrder::getId, orderId)
                        .eq(MwStoreOrder::getStatus,OrderInfoEnum.STATUS_1.getValue()));

				//只有待收货的订单能收货
				if(order != null){
					storeOrderService.takeOrder(order.getOrderId(),null);
					log.info("订单id:{},自动收货成功",body);
				}
			}

			//拼团过期取消
			if(body.contains(ShopConstants.REDIS_PINK_CANCEL_KEY)) {
				body = body.replace(ShopConstants.REDIS_PINK_CANCEL_KEY, "");
				log.info("body:{}",body);
				String pinkId = body;
				MwStorePink storePink = storePinkService.getOne(Wrappers.<MwStorePink>lambdaQuery()
						.eq(MwStorePink::getId,pinkId)
						.eq(MwStorePink::getStatus,OrderInfoEnum.PINK_STATUS_1.getValue())
                        .eq(MwStorePink::getIsRefund,OrderInfoEnum.PINK_REFUND_STATUS_0.getValue()));

				//取消拼团
				if(storePink != null){
					storePinkService.removePink(storePink.getUid(),storePink.getCid(),storePink.getId());
					log.info("拼团订单id:{},未在规定时间完成取消成功",body);
				}
			}
		}

	}
}
