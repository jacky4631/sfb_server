package com.dtk.api.exception;

/**
 * dtk api: result enum
 *
 * @author 1
 * @date 2020/11/10 17:49
 */
public enum DtkResultEnum implements Result {
    UNKNOWN_ERROR(-1, "服务器错误"),
    SUCCESS(0, "成功"),
    URL_NOT_EMPTY(1, "请求地址不能为空"),
    RANK_TYPE_ERROR(10004, "榜单类型为空或不正确"),
    APP_KEY_EMPTY(10016, "appKey不能为空"),
    VERSION_NOT_EMPTY(10017, "版本号不能为空"),
    APP_SECRET_EMPTY(10018, "appSecret不能为空"),


    ;

    private final int code;
    private final String msg;

    DtkResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
