package com.dtk.api.client;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * dtk api: some basic request interface
 *
 * @author 1
 * @date 2020/11/10 10:36
 */
public interface DtkApiRequest<T> {
    /**
     * 获取所有的Key-Value形式的文本请求参数集合。其中：
     * <ul>
     * <li>Key: 请求参数名</li>
     * <li>Value: 请求参数值</li>
     * </ul>
     *  所有实现相同ObjectUtil.objToMap(this)
     *
     * @return 文本请求参数集合
     * @throws IllegalAccessException 反射对象异常
     */
    Map<String, String> getTextParams() throws IllegalAccessException;

    /**
     * 得到当前API接口的版本
     *
     * @return API版本
     */
    String apiVersion();

    /**
     * 得到当前API的响应结果类型
     *
     * @return 响应类型
     */
    TypeReference<T> responseType();

    /**
     * 得到当前API的请求地址
     *
     * @return 请求地址
     */
    String requestUrl();

    void setVersion(String s);
}
