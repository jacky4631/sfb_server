/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.express.config;


import com.mailvor.constant.ShopConstants;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.tools.express.ExpressService;
import com.mailvor.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ExpressAutoConfiguration {


    private static RedisUtils redisUtil;

    @Autowired
    public ExpressAutoConfiguration(RedisUtils redisUtil) {
        ExpressAutoConfiguration.redisUtil = redisUtil;
    }

    public static ExpressService expressService() {
        ExpressService expressService = (ExpressService)redisUtil.get(ShopConstants.MSHOP_EXPRESS_SERVICE);
        if(expressService != null) {
            return expressService;
        }

        ExpressProperties properties = new ExpressProperties();
        String enable = redisUtil.getY("exp_enable");
        String appId = redisUtil.getY("exp_appId");
        String appKey = redisUtil.getY("exp_appKey");
        properties.setAppId(appId);
        properties.setAppKey(appKey);

        if(ShopCommonEnum.ENABLE_2.getValue().toString().equals(enable)){
            properties.setEnable(false);
        }else{
            properties.setEnable(true);
        }
        ExpressService service = new ExpressService();
        service.setProperties(properties);
        redisUtil.set(ShopConstants.MSHOP_EXPRESS_SERVICE,service);
        return service;
    }

}
