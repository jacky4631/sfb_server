package com.mailvor.modules.tk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JdConfig {
    @Value("${jd.appKey}")
    private String appKey;
    @Value("${jd.appSecret}")
    private String appSecret;
    @Value("${jd.server}")
    private String server;
    @Value("${jd.siteId}")
    private String siteId;
    @Value("${jd.unionId}")
    private String unionId;
    @Value("${jd.key}")
    private String key;

}
