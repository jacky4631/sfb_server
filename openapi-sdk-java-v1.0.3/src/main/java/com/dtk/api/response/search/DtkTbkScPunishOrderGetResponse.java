package com.dtk.api.response.search;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description 淘宝违规订单——响应实体
 * @Author FeiGuo
 * @CreateTime 2022-03-31 14:36:41
 * @Version 1.0
 */
@Getter
@Setter
public class DtkTbkScPunishOrderGetResponse {

    @JsonAlias("page_no")
    private Long pageNo;

    @JsonAlias("page_size")
    private Long pageSize;

    @JsonAlias("total_count")
    private Long totalCount;

    @JsonAlias("results")
    private List<FoulOrder> results;

    @Getter
    @Setter
    public static class FoulOrder {
        @JsonAlias("relation_id")
        private Long relationId;
        @JsonAlias("settle_month")
        private String settleMonth;
        @JsonAlias("punish_status")
        private String punishStatus;
        @JsonAlias("violation_type")
        private String violationType;
        @JsonAlias("tk_trade_create_time")
        private String tkTradeCreateTime;
        @JsonAlias("tb_trade_id")
        private String tbTradeId;
        @JsonAlias("tk_adzone_id")
        private String tkAdzoneId;
        @JsonAlias("tk_site_id")
        private String tkSiteId;
        @JsonAlias("tk_pub_id")
        private String tkPubId;
    }
}
