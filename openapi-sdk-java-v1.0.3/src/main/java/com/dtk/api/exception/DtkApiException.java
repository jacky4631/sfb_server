package com.dtk.api.exception;

import lombok.Getter;

/**
 * dtk api: custom business exception
 *
 * @author 1
 * @date 2020/11/10 11:55
 */
@Getter
public class DtkApiException extends RuntimeException {
    private DtkResultEnum dtkResultEnum;

    public DtkApiException(String message) {
        super(message);
    }

    public DtkApiException(DtkResultEnum dtkResultEnum) {
        super(dtkResultEnum.getMsg());
        this.dtkResultEnum = dtkResultEnum;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
