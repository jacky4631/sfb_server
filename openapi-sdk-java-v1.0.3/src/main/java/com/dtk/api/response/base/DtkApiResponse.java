package com.dtk.api.response.base;

import com.dtk.api.exception.DtkResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * dtk api: response data
 *
 * @author 1
 * @date 2020/11/10 10:37
 */
@Getter
@Setter
public class DtkApiResponse<T> {
    @ApiModelProperty(value = "时间戳")
    private final long time = System.currentTimeMillis();
    @ApiModelProperty(value = "状态码")
    private int code;
    @ApiModelProperty(value = "描述信息")
    private String msg;
    @ApiModelProperty(value = "数据")
    private T data;

    public DtkApiResponse() {
    }

    public boolean isSuccess() {
        return this.code == 0;
    }

    public DtkApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public DtkApiResponse(int code, String msg, T date) {
        this.code = code;
        this.msg = msg;
        this.data = date;
    }

    public DtkApiResponse(DtkResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.data = null;
    }

    public static <T> DtkApiResponse<T> buildSuccess(T t) {
        DtkApiResponse<T> result = new DtkApiResponse<>();
        result.setCode(DtkResultEnum.SUCCESS.getCode());
        result.setMsg(DtkResultEnum.SUCCESS.getMsg());
        result.setData(t);
        return result;
    }

    public static <T> DtkApiResponse<T> buildSuccess() {
        return buildSuccess(null);
    }

    public static <T> DtkApiResponse<T> buildFail(Integer code, String msg) {
        DtkApiResponse<T> result = new DtkApiResponse<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> DtkApiResponse<T> buildFail(DtkResultEnum dtkResultEnum) {
        DtkApiResponse<T> result = new DtkApiResponse<>();
        result.setCode(dtkResultEnum.getCode());
        result.setMsg(dtkResultEnum.getMsg());
        return result;
    }

    public static <T> DtkApiResponse<T> buildFail() {
        DtkApiResponse<T> result = new DtkApiResponse<>();
        result.setCode(DtkResultEnum.UNKNOWN_ERROR.getCode());
        result.setMsg(DtkResultEnum.UNKNOWN_ERROR.getMsg());
        return result;
    }
}
