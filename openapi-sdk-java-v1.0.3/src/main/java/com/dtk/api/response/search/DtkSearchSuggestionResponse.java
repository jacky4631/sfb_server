package com.dtk.api.response.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 联想词响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:13
 */
@Getter
@Setter
public class DtkSearchSuggestionResponse {
    @ApiModelProperty(value = "联想词")
    private String kw;
    @ApiModelProperty(value = "对应联想词的商品数量（仅大淘客搜索返回该参数，当使用接口参数为2和3时，不会返回该字段）")
    private Long total;
}
