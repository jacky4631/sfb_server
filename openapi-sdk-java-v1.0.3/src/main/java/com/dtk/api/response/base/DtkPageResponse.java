package com.dtk.api.response.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * dtk api: page param，independent of business entity
 *
 * @author 1
 * @date 2020/11/9 17:15
 */
@Getter
@Setter
public class DtkPageResponse<T> {
    @ApiModelProperty(value = "分页数据")
    private List<T> list;
    @ApiModelProperty(value = "总数量")
    private Long totalNum;
    @ApiModelProperty(value = "页码")
    private String pageId;
}
