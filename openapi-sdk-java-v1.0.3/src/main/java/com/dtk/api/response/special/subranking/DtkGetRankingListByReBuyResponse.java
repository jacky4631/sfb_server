package com.dtk.api.response.special.subranking;

import lombok.Getter;
import lombok.Setter;

/**
 * 各大榜单4(复购榜)响应结果实体
 *
 * @author baige
 * @date 2020/11/25 17:09
 */
@Getter
@Setter
public class DtkGetRankingListByReBuyResponse extends DtkGetRankingListByTimeWholeHotResponse {
    private Integer avgSales;
    private String entryTime;
}
