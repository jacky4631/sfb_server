/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.api;

/**
 * 认证异常
 * @author mazhongjun
 * @date 2020-04-30
 */
public class UnAuthenticatedException extends MshopException {
    public UnAuthenticatedException(String message) {
        super(message);
    }

    public UnAuthenticatedException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public UnAuthenticatedException(ApiCode apiCode) {
        super(apiCode);
    }
}
