package com.mailvor.modules.shanyan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author shenji
 */
@Component
@Data
public class ShanyanConfig{
    @Value("${shanyan.android.appId}")
    private String androidAppId;
    @Value("${shanyan.android.appKey}")
    private String androidAppKey;
    @Value("${shanyan.ios.appId}")
    private String iosAppId;
    @Value("${shanyan.ios.appKey}")
    private String iosAppKey;
    @Value("${shanyan.url.login}")
    private String loginUrl;
}
