/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
@Data
public class PayConfig implements InitializingBean {
    @Value("${pay.goods.title}")
    private String title;
    @Value("${pay.goods.desc}")
    private String desc;

    private String payAdaMode = "delay";

    @Value("${pay.name}")
    private String payName;


    public static String PAY_TITLE;
    public static String PAY_DESC;
    public static String PAY_NAME;

    @Override
    public void afterPropertiesSet() {
        PAY_TITLE = title;
        PAY_DESC = desc;
        PAY_NAME = payName;
    }

    public static String appName() {
        return "苏分宝";
    }
}

