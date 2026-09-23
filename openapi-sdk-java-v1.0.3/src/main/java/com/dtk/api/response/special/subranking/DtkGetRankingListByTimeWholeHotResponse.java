package com.dtk.api.response.special.subranking;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 各大榜单(1(实时榜)，2(全天榜)，3(热推榜))响应结果实体
 *
 * @author baige
 * @date 2020/11/25 16:46
 */
@Getter
@Setter
public class DtkGetRankingListByTimeWholeHotResponse extends DtkRankingBaseResponse {
    private Integer newRankingGoods;
    private Integer ranking;
    private Integer hotPush;
    private String imgs;
    private String guideName;
    private BigDecimal estimateAmount;
}
