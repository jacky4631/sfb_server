package com.dtk.api.response.mastertool;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * 淘系订单查询接口响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:28
 */
@Getter
@Setter
public class DtkGetOrderDetailsResponse {
    @JsonAlias("has_pre")
    private Boolean hasPre;
    @JsonAlias("has_next")
    private Boolean hasNext;
    @JsonAlias("page_no")
    private Integer pageNo;
    @JsonAlias("page_size")
    private Integer pageSize;
    @JsonAlias("position_index")
    private String positionIndex;
    private DtkGetOrderDetailsResultsResponse results;
}
