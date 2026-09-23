package com.dtk.api.response.special;

import com.dtk.api.response.special.subranking.DtkGetHotWordRankingResponse;
import com.dtk.api.response.special.subranking.DtkRankingBaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 各大榜单响应结果实体
 *
 * @author baige
 * @date 2020/11/12 17:09
 */
@Getter
@Setter
public class DtkGetRankingListMergeResponse extends DtkRankingBaseResponse {
    /**
     * 整合实时榜/全天榜/热推榜数据结构
     */
    private BigDecimal estimateAmount;
    private String imgs;
    private String guideName;
    private Integer hotPush;
    private Integer newRankingGoods;
    private Integer ranking;

    /**
     * 整合复购榜数据结构
     */
    private Integer avgSales;
    private String entryTime;

    /**
     * 整合热词飙升榜数据结构
     */
    private Integer top;
    private String keyWord;
    private Integer upVal;
    private List<DtkGetHotWordRankingResponse> goodsList;

    /**
     * 整合热词排行榜数据结构
     */
    private Integer hotVal;

    /**
     * 整合综合热搜榜数据结构
     */
    private Integer fresh;
    private Integer lowest;
    private Integer score;
    private Integer searchNum;

    private List<Integer> marketGroup;
}
