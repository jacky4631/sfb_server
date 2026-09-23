package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 16:41
 */
@Getter
@Setter
public class DtkJdLinkAnalysisResponse {
    @ApiModelProperty("商品ID")
    private String skuId;
    @ApiModelProperty("商品链接地址")
    private String itemUrl;
    @ApiModelProperty("商品是否有优惠券 1-有券 0-无券")
    private Integer hasCoupon;
}
