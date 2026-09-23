package com.dtk.api.response.special.subranking;

import lombok.Getter;
import lombok.Setter;

/**
 * 各大榜单7(综合热搜榜)请求响应结果参数
 *
 * @author baige
 * @date 2020/11/25 17:27
 */
@Getter
@Setter
public class DtkGetRankingListByHotSearchResponse extends DtkRankingBaseResponse {
    private Integer fresh;
    private Integer lowest;
    private Integer score;
    private Integer searchNum;
    private String keyWord;
    private Integer ranking;
}
