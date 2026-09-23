package com.dtk.api.response.putstorage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 失效商品响应结果实体
 *
 * @author 1
 * @date 2020/11/10 16:20
 */
@Getter
@Setter
public class DtkStaleGoodsByTimeResponse {
    @ApiModelProperty(value = "商品id")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;

}
