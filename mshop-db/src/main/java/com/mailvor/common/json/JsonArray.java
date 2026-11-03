/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.json;

import com.mailvor.common.json.JsonUtil;
import com.mailvor.common.json.JsonObject;
import com.mailvor.common.json.JsonArray;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JSON 数组容器，替代原 fastjson 的 {@code JsonArray}。
 * <p>
 * 直接承载 {@link List} 语义，取值方法沿用 fastjson 命名。
 */
public class JsonArray extends ArrayList<Object> {

    private static final long serialVersionUID = 1L;

    public JsonArray() {
        super();
    }

    public JsonArray(Collection<?> collection) {
        super(collection);
    }

    public JsonArray(int initialCapacity) {
        super(initialCapacity);
    }

    public static JsonArray parse(String text) {
        return JsonUtil.parseArray(text);
    }

    // ------------------------------------------------------------------ 取值

    public String getString(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        return value instanceof String ? (String) value : String.valueOf(value);
    }

    public Integer getInteger(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        return value instanceof Number ? ((Number) value).intValue()
                : Integer.valueOf(value.toString().trim());
    }

    public int getIntValue(int index) {
        Integer value = getInteger(index);
        return value == null ? 0 : value;
    }

    public Long getLong(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        return value instanceof Number ? ((Number) value).longValue()
                : Long.valueOf(value.toString().trim());
    }

    public long getLongValue(int index) {
        Long value = getLong(index);
        return value == null ? 0L : value;
    }

    public Double getDouble(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        return value instanceof Number ? ((Number) value).doubleValue()
                : Double.valueOf(value.toString().trim());
    }

    public double getDoubleValue(int index) {
        Double value = getDouble(index);
        return value == null ? 0D : value;
    }

    public Boolean getBoolean(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        return value instanceof Boolean ? (Boolean) value
                : ("1".equals(value.toString()) || Boolean.parseBoolean(value.toString()));
    }

    public boolean getBooleanValue(int index) {
        Boolean value = getBoolean(index);
        return value != null && value;
    }

    public JsonObject getJSONObject(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonObject) {
            return (JsonObject) value;
        }
        return JsonUtil.convert(value, JsonObject.class);
    }

    public JsonArray getJSONArray(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonArray) {
            return (JsonArray) value;
        }
        return JsonUtil.convert(value, JsonArray.class);
    }

    public <T> T getObject(int index, Class<T> clazz) {
        Object value = get(index);
        return value == null ? null : JsonUtil.convert(value, clazz);
    }

    // ------------------------------------------------------------------ 转换

    /** 原 fastjson 的 JsonArray.toJavaList(clazz) */
    public <T> List<T> toJavaList(Class<T> clazz) {
        JavaType type = JsonUtil.mapper().getTypeFactory()
                .constructCollectionType(List.class, clazz);
        return JsonUtil.mapper().convertValue(this, type);
    }

    public <T> List<T> toJavaList(TypeReference<List<T>> type) {
        return JsonUtil.mapper().convertValue(this, type);
    }

    public String toJSONString() {
        return JsonUtil.toJSONString(this);
    }

    @Override
    public JsonArray clone() {
        return new JsonArray(this);
    }
}
