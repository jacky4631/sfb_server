/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TbConfig {
    @Value("${tb.appKey}")
    private String appKey;
    @Value("${tb.appSecret}")
    private String appSecret;
    @Value("${tb.url}")
    private String url;
    @Value("${tb.pid.channelPid}")
    private String channelPid;
    @Value("${tb.pid.tljPid}")
    private String tljPid;
    @Value("${tb.inviterCode}")
    private String inviterCode;

    public Long getAdZoneId() {
        return Long.parseLong(getTljPid().substring(getTljPid().lastIndexOf("_") + 1));
    }
}
