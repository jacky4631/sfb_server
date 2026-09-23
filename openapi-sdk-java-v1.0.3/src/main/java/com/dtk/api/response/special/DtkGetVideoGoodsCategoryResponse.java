package com.dtk.api.response.special;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 短视频商品分类响应结果实体
 *
 * @author baige
 * @date 2020/11/30 17:12
 */
@Data
public class DtkGetVideoGoodsCategoryResponse {
    @ApiModelProperty(value = "分类id")
    private Integer id;

    @ApiModelProperty(value = "分类名称")
    private String name;
}
