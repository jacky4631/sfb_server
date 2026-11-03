/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.json;

import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * 多态反序列化的类型白名单，替代原 fastjson 的
 * {@code ParserConfig.getGlobalInstance().setAutoTypeSupport(true)}。
 * <p>
 * 全局开启 autoType 是 fastjson 历史上的远程代码执行漏洞根源，这里改为显式白名单：
 * 只允许业务包与 JDK 基础容器/数值类型参与 {@code @type} 还原。
 * 新增缓存对象类型时，必须确认其包名落在 {@code com.mailvor} 之下，
 * 否则在此处补充条目而不是放宽范围。
 */
public final class PolymorphicWhitelist {

    /** 业务对象 */
    private static final String BUSINESS_PACKAGE = "com.mailvor";

    private PolymorphicWhitelist() {
    }

    public static PolymorphicTypeValidator validator() {
        return BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(BUSINESS_PACKAGE)
                .allowIfSubType("java.util.")
                .allowIfSubType("java.lang.")
                .allowIfSubType("java.math.")
                .allowIfSubType("java.time.")
                .allowIfSubType("java.sql.")
                .allowIfSubTypeIsArray()
                .build();
    }
}
