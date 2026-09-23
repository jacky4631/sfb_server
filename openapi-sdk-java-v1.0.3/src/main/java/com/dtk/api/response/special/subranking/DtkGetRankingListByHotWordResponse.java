package com.dtk.api.response.special.subranking;

import lombok.Data;

import java.util.List;

/**
 * 各大榜单6(热词排行榜)响应结果参数
 *
 * @author baige
 * @date 2020/11/25 17:28
 */
@Data
public class DtkGetRankingListByHotWordResponse {
    private Integer top;
    private String keyWord;
    private Integer hotVal;
    private List<DtkGetHotWordRankingResponse> goodsList;
}
