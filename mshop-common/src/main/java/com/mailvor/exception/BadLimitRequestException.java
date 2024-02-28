/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.exception;

import com.mailvor.api.ApiCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 统一异常处理
 */
@Getter
public class BadLimitRequestException extends RuntimeException{

    private Integer status = ApiCode.BAD_LIMIT_EXCEPTION.getCode();

    public BadLimitRequestException(String msg){
        super(msg);
    }

    public BadLimitRequestException(HttpStatus status, String msg){
        super(msg);
        this.status = status.value();
    }
}
