/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Data
public class DataokeConfig {
    @Value("${dataoke.key}")
    private String key;
    @Value("${dataoke.secret}")
    private String secret;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        // 原先这里构造了 FastJsonHttpMessageConverter，但把它注册进 converters 列表的那行一直是注释状态，
        // 即 fastjson 从未生效，RestTemplate 实际用的就是默认 Jackson 转换器；
        // 升级后直接删除这段死代码，行为不变
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //ms
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        return factory;
    }
}
