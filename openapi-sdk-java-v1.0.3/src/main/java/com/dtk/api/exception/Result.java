package com.dtk.api.exception;

/**
 * dtk api: result abstract interface
 *
 * @author 1
 * @date 2020/11/10 17:49
 */
public interface Result {
    /**
     * 获取结果code
     *
     * @return code
     */
    int getCode();

    /**
     * 获取结果信息
     *
     * @return message
     */
    String getMsg();
}
