/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.json;

import com.mailvor.common.json.JsonUtil;
import com.mailvor.common.json.JsonObject;
import com.mailvor.common.json.JsonArray;
import tools.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON 对象容器，替代原 fastjson 的 {@code JsonObject}。
 * <p>
 * 继承 {@link LinkedHashMap} 以保证字段顺序与插入顺序一致（与 fastjson 默认行为相同），
 * 取值方法沿用 fastjson 命名，迁移时调用点无需改写。
 * <p>
 * 取值语义：键不存在或值为 null 时，包装类型返回 null，{@code getXxxValue} 系列返回类型零值；
 * 类型转换失败抛出运行时异常，与原 fastjson 一致，不做静默吞异常。
 */
public class JsonObject extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public JsonObject() {
        super();
    }

    public JsonObject(Map<String, Object> map) {
        super(map);
    }

    /** 兼容 fastjson 的 new JsonObject()（本类本就是有序 Map，参数无意义） */
    public JsonObject(boolean ordered) {
        super();
    }

    public static JsonObject parse(String text) {
        return JsonUtil.parseObject(text);
    }

    /** 保留原 fastjson 的方法名，便于 {@code JsonObject::parseObject} 这类方法引用继续可用 */
    public static JsonObject parseObject(String text) {
        return JsonUtil.parseObject(text);
    }

    // ------------------------------------------------------------------ 取值

    public String getString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        return value instanceof String ? (String) value : String.valueOf(value);
    }

    public Integer getInteger(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return new BigDecimal(str).intValue();
        }
    }

    public int getIntValue(String key) {
        Integer value = getInteger(key);
        return value == null ? 0 : value;
    }

    public Long getLong(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        return new BigDecimal(str).longValue();
    }

    public long getLongValue(String key) {
        Long value = getLong(key);
        return value == null ? 0L : value;
    }

    public Double getDouble(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        String str = value.toString().trim();
        return str.isEmpty() ? null : Double.valueOf(str);
    }

    public double getDoubleValue(String key) {
        Double value = getDouble(key);
        return value == null ? 0D : value;
    }

    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        return "1".equals(str) || "true".equalsIgnoreCase(str);
    }

    public boolean getBooleanValue(String key) {
        Boolean value = getBoolean(key);
        return value != null && value;
    }

    public BigDecimal getBigDecimal(String key) {
        return JsonUtil.toBigDecimal(get(key));
    }

    /**
     * 取日期值，兼容原 fastjson 的 getDate(key)：
     * 时间戳按毫秒处理，字符串按 {@code yyyy-MM-dd HH:mm:ss} 及 ISO 常见形式解析。
     */
    public java.util.Date getDate(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Date) {
            return (java.util.Date) value;
        }
        if (value instanceof Number) {
            return new java.util.Date(((Number) value).longValue());
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        try {
            return java.sql.Timestamp.valueOf(str.replace('T', ' '));
        } catch (IllegalArgumentException e) {
            return cn.hutool.core.date.DateUtil.parse(str);
        }
    }

    public JsonObject getJSONObject(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonObject) {
            return (JsonObject) value;
        }
        if (value instanceof Map) {
            return new JsonObject(castMap(value));
        }
        if (value instanceof String) {
            return JsonUtil.parseObject((String) value);
        }
        return JsonUtil.convert(value, JsonObject.class);
    }

    public JsonArray getJSONArray(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonArray) {
            return (JsonArray) value;
        }
        if (value instanceof List) {
            return new JsonArray(castList(value));
        }
        if (value instanceof String) {
            return JsonUtil.parseArray((String) value);
        }
        return JsonUtil.convert(value, JsonArray.class);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        Object value = get(key);
        return value == null ? null : JsonUtil.convert(value, clazz);
    }

    public <T> T getObject(String key, TypeReference<T> type) {
        Object value = get(key);
        return value == null ? null : JsonUtil.convert(value, type);
    }

    // ------------------------------------------------------------------ 转换

    /** 原 fastjson 的 JsonObject.toJavaObject(clazz) */
    public <T> T toJavaObject(Class<T> clazz) {
        return JsonUtil.convert(this, clazz);
    }

    public String toJSONString() {
        return JsonUtil.toJSONString(this);
    }

    @Override
    public JsonObject clone() {
        return new JsonObject(this);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> castList(Object value) {
        return (List<Object>) value;
    }
}
