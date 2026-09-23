package com.dtk.api.response.special.subranking;

import lombok.Data;

import java.util.List;

/**
 * 各大榜单5(热词飙升榜)响应结果实体
 *
 * @author baige
 * @date 2020/11/25 17:28
 */
@Data
public class DtkGetRankingListByHotWordUpResponse {
    private Integer top;
    private String keyWord;
    private Integer upVal;
    private List<DtkGetHotWordRankingResponse> goodsList;
}
