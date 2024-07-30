/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 *
 */
package com.mailvor.modules.tk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PddConfig {

    @Value("${pdd.clientId}")
    private String clientId;
    @Value("${pdd.clientSecret}")
    private String clientSecret;

    @Value("${pdd.pid}")
    private String pid;


    public String getParam(Long uid) {
        return "{\"uid\":\""+uid + "\"}";
    }

}
