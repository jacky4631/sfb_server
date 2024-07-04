package com.mailvor.modules.meituan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MeituanConfig {
    @Value(("${meituan.appKey}"))
    private String appKey;

    @Value(("${meituan.appSecret}"))
    private String appSecret;
}
