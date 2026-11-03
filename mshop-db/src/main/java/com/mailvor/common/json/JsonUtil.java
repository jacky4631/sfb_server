/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.json;

import com.mailvor.common.json.JsonUtil;
import com.mailvor.common.json.JsonObject;
import com.mailvor.common.json.JsonArray;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 全项目统一的 JSON 出入口，基于 Jackson 3（Spring Boot 4 默认实现）。
 * <p>
 * 替代原 fastjson 1.2.83 的静态门面，方法名与 fastjson 保持一致，
 * 便于批量迁移时调用点形状不变。语义上刻意对齐 fastjson 的默认行为，
 * 以免升级改变接口响应契约：
 * <ul>
 *     <li>null 字段不输出（fastjson 默认 NON_NULL）</li>
 *     <li>日期格式 yyyy-MM-dd HH:mm:ss（fastjson 默认）</li>
 *     <li>未知字段不报错（对齐 fastjson 宽松解析）</li>
 *     <li>空白输入返回 null 而非抛异常（对齐 fastjson）</li>
 * </ul>
 * 需要类型信息参与序列化的场景（如 Redis 缓存）请使用
 * {@link #typedMapper()}，不要改动本类的默认 mapper。
 */
public final class JsonUtil {

    /** 与原 fastjson 默认行为一致的日期格式 */
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final ObjectMapper DEFAULT = build(
            JsonMapper.builder().defaultDateFormat(new SimpleDateFormat(DATE_PATTERN)));

    /** 带 @type 类型信息的 mapper，供 Redis 等需要还原具体类型的场景使用 */
    private static final ObjectMapper TYPED = buildTyped();

    private JsonUtil() {
    }

    private static ObjectMapper build(JsonMapper.Builder builder) {
        SimpleModule javaTimeFormat = new SimpleModule()
                // java.time 默认输出 ISO-8601（带 T），fastjson 输出 "yyyy-MM-dd HH:mm:ss"，
                // 这里统一成后者，避免升级后接口响应里的时间字段格式变化
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME))
                .addSerializer(LocalDate.class, new LocalDateSerializer(DATE))
                .addSerializer(LocalTime.class, new LocalTimeSerializer(TIME));
        return builder
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_NULL)
                                .withContentInclusion(JsonInclude.Include.NON_NULL))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // Jackson 3 把该开关从 SerializationFeature 移到了 DateTimeFeature
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addModule(javaTimeFormat)
                .build();
    }

    private static ObjectMapper buildTyped() {
        return build(JsonMapper.builder()
                // 属性名用 @type，与线上既有 fastjson 缓存数据保持一致
                .activateDefaultTypingAsProperty(
                        PolymorphicWhitelist.validator(), tools.jackson.databind.DefaultTyping.NON_FINAL,
                        "@type"));
    }

    public static ObjectMapper mapper() {
        return DEFAULT;
    }

    public static ObjectMapper typedMapper() {
        return TYPED;
    }

    // ------------------------------------------------------------------ 序列化

    public static String toJSONString(Object obj) {
        return obj == null ? null : DEFAULT.writeValueAsString(obj);
    }

    public static String toJSONString(Object obj, boolean pretty) {
        return obj == null ? null
                : (pretty ? DEFAULT.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
                : DEFAULT.writeValueAsString(obj));
    }

    // ------------------------------------------------------------------ 反序列化

    /** 解析为 JsonObject，空白输入返回 null */
    public static JsonObject parseObject(String text) {
        if (isBlank(text)) {
            return null;
        }
        Map<String, Object> map = DEFAULT.readValue(text, new TypeReference<Map<String, Object>>() {
        });
        return map == null ? null : new JsonObject(map);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return isBlank(text) ? null : DEFAULT.readValue(text, clazz);
    }

    public static <T> T parseObject(String text, TypeReference<T> type) {
        return isBlank(text) ? null : DEFAULT.readValue(text, type);
    }

    /** 原 fastjson 的 JsonUtil.toJSON(obj)：把 POJO 转成 JsonObject */
    public static JsonObject toJSON(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JsonObject) {
            return (JsonObject) obj;
        }
        // 先转成普通 Map 再过 JsonObject 构造器，避免 Jackson 直接填充时跳过嵌套归一化
        return new JsonObject(DEFAULT.convertValue(obj, new TypeReference<Map<String, Object>>() {
        }));
    }

    public static JsonArray parseArray(String text) {
        if (isBlank(text)) {
            return null;
        }
        List<Object> list = DEFAULT.readValue(text, new TypeReference<List<Object>>() {
        });
        return list == null ? null : new JsonArray(list);
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (isBlank(text)) {
            return null;
        }
        JavaType type = DEFAULT.getTypeFactory().constructCollectionType(List.class, clazz);
        return DEFAULT.readValue(text, type);
    }

    /** 结构未知时的通用入口：JSON 对象转 JsonObject，数组转 JsonArray，标量原样返回 */
    public static Object parse(String text) {
        if (isBlank(text)) {
            return null;
        }
        JsonNode node = DEFAULT.readTree(text);
        if (node.isObject()) {
            return new JsonObject(DEFAULT.convertValue(node, new TypeReference<Map<String, Object>>() {
            }));
        }
        if (node.isArray()) {
            return new JsonArray(DEFAULT.convertValue(node, new TypeReference<List<Object>>() {
            }));
        }
        if (node.isString()) {
            return node.asString();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isNull()) {
            return null;
        }
        return node.isIntegralNumber() ? (Object) node.asLong() : (Object) node.asDouble();
    }

    /** POJO 之间的结构转换（原 fastjson 的 JSON.toJavaObject） */
    public static <T> T toJavaObject(Object from, Class<T> clazz) {
        return from == null ? null : DEFAULT.convertValue(from, clazz);
    }

    public static <T> T convert(Object from, Class<T> clazz) {
        return from == null ? null : DEFAULT.convertValue(from, clazz);
    }

    public static <T> T convert(Object from, TypeReference<T> type) {
        return from == null ? null : DEFAULT.convertValue(from, type);
    }

    public static JsonNode readTree(String text) {
        return isBlank(text) ? null : DEFAULT.readTree(text);
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        String str = value.toString().trim();
        return str.isEmpty() ? null : new BigDecimal(str);
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
