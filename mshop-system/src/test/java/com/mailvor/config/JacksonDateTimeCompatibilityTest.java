package com.mailvor.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 锁定升级后的日期序列化契约。
 * <p>
 * Spring Boot 2.7（Jackson 2）把 java.util.Date 序列化成 epoch 毫秒数字，
 * Jackson 3 默认改成 ISO-8601 字符串。前端按数字解析，所以
 * {@link JacksonConfig} 必须把这个默认值改回去；本测试同时记录两侧默认行为，
 * 一旦有人删掉 customizer，这里会先失败而不是等到线上解析报错。
 */
class JacksonDateTimeCompatibilityTest {

    private static final long EPOCH_MILLIS = 1793710574915L;

    private Map<String, Date> payload() {
        Map<String, Date> map = new LinkedHashMap<>();
        map.put("time", new Date(EPOCH_MILLIS));
        return map;
    }

    @Test
    @DisplayName("Jackson 3 裸默认值把 Date 写成了 ISO 字符串（这就是升级带来的差异）")
    void jackson3DefaultWritesIsoString() {
        ObjectMapper plain = JsonMapper.builder().build();
        String json = plain.writeValueAsString(payload());
        // 形如 {"time":"2026-11-03T12:56:14.915Z"}，不再是 {"time":1793710574915}
        assertTrue(json.contains("T") && json.contains("\"time\":\""),
                "Jackson 3 默认应输出 ISO 字符串，实际: " + json);
    }

    @Test
    @DisplayName("应用 JacksonConfig 的 customizer 后，Date 恢复为 epoch 毫秒数字")
    void customizerRestoresEpochMillis() {
        JsonMapper.Builder builder = JsonMapper.builder();
        new JacksonConfig().writeDatesAsTimestampsCustomizer().customize(builder);
        String json = builder.build().writeValueAsString(payload());
        assertEquals("{\"time\":" + EPOCH_MILLIS + "}", json);
    }
}
