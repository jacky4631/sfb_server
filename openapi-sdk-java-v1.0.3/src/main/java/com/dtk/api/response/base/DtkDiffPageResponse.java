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
public class DtkDiffPageResponse<T> {
    @ApiModelProperty(value = "分页数据")
    private List<T> lists;
    @ApiModelProperty(value = "总记录数")
    private Integer totalCount;
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;
    @ApiModelProperty(value = "当前页")
    private Integer currentPage;
}
