/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.DateTimeFeature;

/**
 * MVC 层 JSON 序列化的兼容性约束。
 * <p>
 * Spring Boot 2.7 用的 Jackson 2 默认把 {@code java.util.Date} 写成 epoch 毫秒数字；
 * Jackson 3 把 {@link DateTimeFeature#WRITE_DATES_AS_TIMESTAMPS} 的默认值翻转成了关闭，
 * 于是没标 {@code @JsonFormat} 的 Date 字段会变成 ISO-8601 字符串。
 * 项目里有 75 个这样的 DTO/VO 字段（分布在 37 个类），APP 与后台前端按数字解析，
 * 直接切换会造成线上解析失败，这里显式恢复旧行为。
 * <p>
 * 用代码而不是 {@code spring.jackson.*} 配置项，是为了让这条约束跟着类走、可被单测覆盖
 * （见 {@code JacksonDateTimeCompatibilityTest}），不依赖属性绑定的隐式行为。
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer writeDatesAsTimestampsCustomizer() {
        return builder -> builder.enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
