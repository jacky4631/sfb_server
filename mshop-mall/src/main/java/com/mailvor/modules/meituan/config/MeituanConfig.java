package com.mailvor.modules.meituan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MeituanConfig {
    @Value(("${meituan.utmSource}"))
    private String utmSource;
    @Value(("${meituan.appKey}"))
    private String appKey;


    @Value(("${meituan.promotionId}"))
    private String promotionId;

}
