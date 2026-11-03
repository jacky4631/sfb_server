/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.dozer.service;

import cn.hutool.core.bean.BeanUtil;
import com.mailvor.common.web.vo.Paging;
import com.mailvor.domain.PageResult;
import com.github.pagehelper.PageInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ：LionCity
 * @date ：Created in 2019/10/9 10:43
 * @description：实体 / DTO 转换实现
 * @modified By：
 * @version: 1.0
 */
@Component
@Lazy()
public class EJBGenerator implements IGenerator {

    /**
     * 原实现注入 dozer 的 {@code Mapper} 做属性拷贝。Dozer 5.5.1 自 2014 年起停止维护，
     * 依赖 javax 与老版 commons-beanutils，无法在 JDK 25 / Spring Boot 4 上运行，
     * 这里改用项目里已有的 hutool {@link BeanUtil}（同名属性拷贝，与 Dozer 无映射文件时的
     * 约定式行为一致），对外方法签名保持不变，126 处注入点无需改动。
     * <p>
     * 已知差异：泛型元素类型不同的嵌套集合（如 {@code List<A>} 转 {@code List<B>}），
     * Dozer 会逐元素递归映射，hutool 只按属性名赋值。回归测试要重点看这类转换。
     */
    private <T, S> T map(final S s, Class<T> clz) {
        return s == null ? null : BeanUtil.copyProperties(s, clz);
    }

    @Override
    public <T, S> T convert(final S s, Class<T> clz) {
        return map(s, clz);
    }

    @Override
    public <T, S> List<T> convert(List<S> s, Class<T> clz) {
        return s == null ? null : s.stream().map(vs -> map(vs, clz)).collect(Collectors.toList());
    }

    @Override
    public <T, S> Paging<T> convertPaging(Paging<S> paging, Class<T> clz) {
        Paging<T> pagingVo = new Paging<T>();
        pagingVo.setRecords(convert(paging.getRecords(), clz));
        pagingVo.setTotal(paging.getTotal());
        return pagingVo;
    }

    @Override
    public <T, S> Set<T> convert(Set<S> s, Class<T> clz) {
        return s == null ? null : s.stream().map(vs -> map(vs, clz)).collect(Collectors.toSet());
    }

    @Override
    public <T, S> T[] convert(S[] s, Class<T> clz) {
        if (s == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(clz, s.length);
        for (int i = 0; i < s.length; i++) {
            arr[i] = map(s[i], clz);
        }
        return arr;
    }

    @Override
    public <T, S> PageResult<T> convertPageInfo(PageInfo<S> s, Class<T> clz) {
        return new PageResult(s.getTotal(), convert(s.getList(), clz));
    }
}
