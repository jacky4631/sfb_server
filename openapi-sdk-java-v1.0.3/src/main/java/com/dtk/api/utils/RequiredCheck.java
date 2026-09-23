package com.dtk.api.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DTK SDK请求参数必填校验
 *
 * @author:YSH
 * @date: 2021/7/2
 * @time: 16:13
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredCheck {
}
