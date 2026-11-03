/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.json;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import tools.jackson.databind.JavaType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * JSON 字段与实体属性之间的 MyBatis 类型处理器，替代 MP 自带的
 * {@code FastjsonTypeHandler}。
 * <p>
 * MP 3.5.17 提供的三个内置处理器分别绑定 fastjson1（已随本次升级移除）、
 * Jackson 2（会与 Spring Boot 4 的 Jackson 3 形成双栈）和 gson，
 * 这里统一走项目自己的 {@link JsonUtil}，保证全项目只有一套 JSON 实现。
 * <p>
 * 兼容性：库里存的仍是普通 JSON 文本（原 FastjsonTypeHandler 写库时没有开启
 * WriteClassName，不含 {@code @type}），因此无需数据迁移。
 */
public class JsonTypeHandler extends AbstractJsonTypeHandler<Object> {

    public JsonTypeHandler(Class<?> type) {
        super(type);
    }

    public JsonTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return JsonUtil.mapper().readValue(json, javaType());
    }

    @Override
    public String toJson(Object obj) {
        return JsonUtil.toJSONString(obj);
    }

    /**
     * 字段声明为 {@code List<Xxx>} 这类泛型时，只靠 Class 拿不到元素类型，
     * 必须用 MP 传进来的 genericType 构造 JavaType，否则反序列化出来是 LinkedHashMap。
     */
    private JavaType javaType() {
        Type generic = getFieldType();
        if (generic != null && generic != type) {
            return JsonUtil.mapper().getTypeFactory().constructType(generic);
        }
        return JsonUtil.mapper().constructType(type);
    }
}
