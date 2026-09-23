package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 17:02
 */
@Getter
@Setter
public class DtkJdCommodityTransformLinkResponse {
    @ApiModelProperty("商品转链后的短链接")
    private String shortUrl;
    @ApiModelProperty("商品转链后的长链接")
    private String longUrl;
}
