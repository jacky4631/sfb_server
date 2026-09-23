package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 我的收藏响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:59
 */
@Getter
@Setter
public class DtkGetCollectionListItemResponse {
    @ApiModelProperty(value = "商品id")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;
    @ApiModelProperty(value = "是否预估商品：0为一般商品，1为预告商品")
    private Integer trailerGoods;
}
