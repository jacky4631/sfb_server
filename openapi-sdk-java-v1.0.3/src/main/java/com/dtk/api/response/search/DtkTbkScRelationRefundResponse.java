package com.dtk.api.response.search;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description 淘宝维权订单——响应实体
 * @Author FeiGuo
 * @CreateTime 2022-03-31 14:18:07
 * @Version 1.0
 */
@Getter
@Setter
public class DtkTbkScRelationRefundResponse {

    @JsonAlias("page_no")
    private Long pageNo;

    @JsonAlias("page_size")
    private Long pageSize;

    @JsonAlias("total_count")
    private Long totalCount;

    @JsonAlias("results")
    private List<RefundOrder> results;

    @Getter
    @Setter
    public static class RefundOrder {
        @JsonAlias("relation_id")
        private Long relationId;
        @JsonAlias("special_id")
        private Long specialId;
        @JsonAlias("tb_trade_parent_id")
        private String tbTradeParentId;
        @JsonAlias("tk3rd_pub_id")
        private String tk3rdPubId;
        @JsonAlias("tk_pub_id")
        private String tkPubId;
        @JsonAlias("tk_subsidy_fee_refund3rd_pub")
        private String tkSubsidyFeeRefund3rdPub;
        @JsonAlias("tk_commission_fee_refund3rd_pub")
        private String tkCommissionFeeRefund3rdPub;
        @JsonAlias("tk_subsidy_fee_refund_pub")
        private String tkSubsidyFeeRefundPub;
        @JsonAlias("tk_commission_fee_refund_pub")
        private String tkCommissionFeeRefundPub;
        @JsonAlias("tk_refund_suit_time")
        private String tkRefundSuitTime;
        @JsonAlias("tk_refund_time")
        private String tkRefundTime;
        @JsonAlias("earning_time")
        private String earningTime;
        @JsonAlias("tb_trade_create_time")
        private String tbTradeCreateTime;
        @JsonAlias("refund_status")
        private Integer refundStatus;
        @JsonAlias("tb_auction_title")
        private String tbAuctionTitle;
        @JsonAlias("tb_trade_id")
        private String tbTradeId;
        @JsonAlias("refund_fee")
        private String refundFee;
        @JsonAlias("tb_trade_finish_price")
        private String tbTradeFinishPrice;
        @JsonAlias("tk_pub_show_return_fee")
        private String tkPubShowReturnFee;
        @JsonAlias("tk3rd_pub_show_return_fee")
        private String tk3rdPubShowReturnFee;
        @JsonAlias("refund_type")
        private Integer refundType;
        @JsonAlias("alsc_pid")
        private String alscPid;
        @JsonAlias("alsc_id")
        private String alscId;
        @JsonAlias("modified_time")
        private String modifiedTime;
    }
}
