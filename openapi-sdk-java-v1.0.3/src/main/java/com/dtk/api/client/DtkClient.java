package com.dtk.api.client;

import com.dtk.api.exception.DtkApiException;

/**
 * dtk api: DtkClient,Provide an execution interface
 *
 * @author 1
 * @date 2020/11/10 11:24
 */
public interface DtkClient {
    /**
     * 根据请求参数执行逻辑
     *
     * @param request 请求参数
     * @param <T>     请求泛型
     * @return 返回结果与request对应
     * @throws DtkApiException dtk自定义异常
     */
    <T> T execute(DtkApiRequest<T> request);
}
