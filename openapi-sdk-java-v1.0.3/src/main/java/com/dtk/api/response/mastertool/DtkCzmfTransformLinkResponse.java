package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 17:48
 */
@Getter
@Setter
public class DtkCzmfTransformLinkResponse {
    @ApiModelProperty("跳转领券页淘口令")
    private String couponCode;
    @ApiModelProperty("跳转详情页淘口令")
    private String orderCode;
}
