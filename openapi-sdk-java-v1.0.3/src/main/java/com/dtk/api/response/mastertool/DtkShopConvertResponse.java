package com.dtk.api.response.mastertool;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店铺转链响应结果实体
 *
 * @author baige
 * @date 2020/11/30 11:00
 */
@Data
public class DtkShopConvertResponse {
    @ApiModelProperty(value = "推广链接地址")
    private String shopLinks;

    @ApiModelProperty(value = "淘口令")
    @JsonProperty("Tpwd")
    private String Tpwd;

    @ApiModelProperty(value = "长口令")
    private String longTpwd;
}
