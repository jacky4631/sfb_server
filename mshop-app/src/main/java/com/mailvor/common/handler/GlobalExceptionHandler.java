/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.handler;


import com.mailvor.api.ApiCode;
import com.mailvor.api.ApiResult;
import com.mailvor.api.BusinessException;
import com.mailvor.api.DaoException;
import com.mailvor.api.UnAuthenticatedException;
import com.mailvor.api.MshopException;
import com.mailvor.common.bean.RequestDetail;
import com.mailvor.common.util.RequestDetailThreadLocal;
import com.mailvor.exception.BadLimitRequestException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 全局异常
 * @author huangyu
 * @date 2020-04-30
 */
@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    public static final List<String> UNLOG_STRING = Arrays.asList("订单不存在", "订单已失效", "非法访问");

    /**
     * 非法参数验证异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResult<String> handleMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        printRequestDetail();
        BindingResult bindingResult = ex.getBindingResult();
        List<String> list = new ArrayList<>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            list.add(fieldError.getDefaultMessage());
        }

        Collections.sort(list);
        String msg = "不能为空";
        if(!list.isEmpty()) {
            msg = list.get(0);
        }
        log.error(getApiCodeString(ApiCode.PARAMETER_EXCEPTION) + ":" + JSON.toJSONString(list));
        return ApiResult.fail(ApiCode.PARAMETER_EXCEPTION.getCode(), msg);
    }




    @ExceptionHandler(value = BadLimitRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Boolean> badLimitRequestException(BadLimitRequestException exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.SYSTEM_EXCEPTION, exception);
        return new ApiResult<Boolean>()
                .setStatus(ApiCode.BAD_LIMIT_EXCEPTION.getCode())
                .setMsg(exception.getMessage());
    }



    /**
     * 自定义业务/数据异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {MshopException.class})
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> MshopException(MshopException exception) {
        printRequestDetail();
        if(shouldLogStack(exception)) {
            log.error("MshopException:", exception);
        }
        int errorCode;
        if (exception instanceof BusinessException) {
            errorCode = ApiCode.BUSINESS_EXCEPTION.getCode();
        } else if (exception instanceof DaoException) {
            errorCode = ApiCode.DAO_EXCEPTION.getCode();
        } else if (exception instanceof UnAuthenticatedException) {
            errorCode = ApiCode.UNAUTHORIZED.getCode();

        } else {
            errorCode = ApiCode.BUSINESS_EXCEPTION.getCode();
        }
        System.out.println("=======");
        return new ApiResult<Boolean>()
                .setStatus(errorCode)
                .setMsg(exception.getMessage());
    }

    protected boolean shouldLogStack(MshopException exception) {
        String msg = exception.getMessage();

        return !UNLOG_STRING.contains(msg);
    }



    /**
     * 默认的异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<String> httpRequestMethodNotSupportedExceptionHandler(Exception exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION, exception);
        return ApiResult.fail(ApiCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION.getCode(), exception.getMessage());
    }

    /**
     * 默认的异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Boolean> exceptionHandler(Exception exception) {
        printRequestDetail();
        printApiCodeException(ApiCode.SYSTEM_EXCEPTION, exception);
       // return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION);

        return new ApiResult<Boolean>()
                .setStatus(ApiCode.FAIL.getCode())
                .setMsg(exception.getMessage());
    }


    /**
     * 打印请求详情
     */
    private void printRequestDetail() {
        RequestDetail requestDetail = RequestDetailThreadLocal.getRequestDetail();
        if (requestDetail != null) {
            log.error("异常来源：ip: {}, path: {}", requestDetail.getIp(), requestDetail.getPath());
        }
    }

    /**
     * 获取ApiCode格式化字符串
     *
     * @param apiCode
     * @return
     */
    private String getApiCodeString(ApiCode apiCode) {
        if (apiCode != null) {
            return String.format("errorCode: %s, errorMessage: %s", apiCode.getCode(), apiCode.getMessage());
        }
        return null;
    }

    /**
     * 打印错误码及异常
     *
     * @param apiCode
     * @param exception
     */
    private void printApiCodeException(ApiCode apiCode, Exception exception) {
        log.error(getApiCodeString(apiCode), exception);
        exception.printStackTrace();
    }

}
