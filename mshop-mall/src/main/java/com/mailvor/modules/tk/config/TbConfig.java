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
    @Value("${tb.inviterCode}")
    private String inviterCode;
}
