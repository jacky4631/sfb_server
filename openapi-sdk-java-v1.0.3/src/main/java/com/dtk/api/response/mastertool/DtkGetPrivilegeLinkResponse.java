package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 高效转链响应结果实体
 *
 * @author 1
 * @date 2020/11/30 15:38
 */
@Data
public class DtkGetPrivilegeLinkResponse {
    private String couponClickUrl;

    private String couponEndTime;

    private String couponInfo;

    private String couponStartTime;

    private String itemId;

    private String couponTotalCount;

    private String couponRemainCount;

    @ApiModelProperty(value = "商品淘客链接")
    private String itemUrl;

    @ApiModelProperty(value = "淘口令")
    private String tpwd;

    @ApiModelProperty(value = "佣金比率")
    private String maxCommissionRate;

    @ApiModelProperty(value = "短链接")
    private String shortUrl;

    @ApiModelProperty(value = "当传入请求参数channelId、specialId、externalId时，该字段展示预估最低佣金率(%)")
    private String minCommissionRate;

    @ApiModelProperty(value = "针对iOS14版本，增加对应能解析的长口令")
    private String longTpwd;

    @ApiModelProperty(value = "微信防封二维码")
    private String kuaiZhanUrl;

    @ApiModelProperty(value = "商品原价(2020/12/30新增字段)")
    private String originalPrice;

    @ApiModelProperty(value = "券后价(2020/12/30新增字段)")
    private String actualPrice;

}
