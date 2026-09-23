package com.dtk.api.request.base;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * dtk api: url param，independent of business entity
 *
 * @author 1
 * @date 2020/11/9 17:15
 */
@Getter
@Setter
public class DtkUrlParamRequest {
    @ApiModelProperty(value = "请求地址", hidden = true)
    private String requestUrl;
}
