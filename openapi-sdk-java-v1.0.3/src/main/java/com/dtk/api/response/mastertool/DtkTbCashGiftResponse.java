package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 10:48
 */
@Getter
@Setter
public class DtkTbCashGiftResponse {
    @ApiModelProperty("淘礼金ID")
    private String rightsId;
    @ApiModelProperty("淘礼金领取地址")
    private String sendUrl;
    @ApiModelProperty("淘口令")
    private String tpwd;
    @ApiModelProperty("长口令（针对iOS14）")
    private String longTpwd;
}
