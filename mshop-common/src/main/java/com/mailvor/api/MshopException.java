/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.api;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常
 * @author mazhongjun
 * @date 2020-04-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MshopException extends RuntimeException{

    private static final long serialVersionUID = -2470461654663264392L;

    private Integer errorCode;
    private String message;

    public MshopException() {
        super();
    }

    public MshopException(String message) {
        super(message);
        this.message = message;
    }

    public MshopException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public MshopException(ApiCode apiCode) {
        super(apiCode.getMessage());
        this.errorCode = apiCode.getCode();
        this.message = apiCode.getMessage();
    }

    public MshopException(String message, Throwable cause) {
        super(message, cause);
    }

    public MshopException(Throwable cause) {
        super(cause);
    }

}
