/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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


    @JsonIgnore
    public List<String> getAdZoneIds() {
        // 把渠道pid 0元购pid和动态pid的第三段加到list
        List<String> list = new ArrayList<>();
        if(StringUtils.isNotBlank(channelPid)) {
            list.add(channelPid.substring(channelPid.lastIndexOf("_") + 1));
        }
        if(StringUtils.isNotBlank(tljPid)) {
            list.add(tljPid.substring(tljPid.lastIndexOf("_") + 1));
        }
        return list;
    }
}
