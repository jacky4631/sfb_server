package com.mailvor.modules.dataoke.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pdd.pop.ext.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListGetResponseOrderListDTO {
    @JsonProperty("activity_tags")
    private List<Integer> activityTags;
    @JsonProperty("auth_duo_id")
    private Long authDuoId;
    @JsonProperty("bandan_risk_consult")
    private Integer bandanRiskConsult;
    @JsonProperty("batch_no")
    private String batchNo;
    @JsonProperty("cash_gift_id")
    private Long cashGiftId;
    @JsonProperty("cat_ids")
    private List<Long> catIds;
    @JsonProperty("cpa_new")
    private Integer cpaNew;
    @JsonProperty("custom_parameters")
    private String customParameters;
    @JsonProperty("fail_reason")
    private String failReason;
    @JsonProperty("goods_category_name")
    private String goodsCategoryName;
    @JsonProperty("goods_id")
    private Long goodsId;
    @JsonProperty("goods_name")
    private String goodsName;
    @JsonProperty("goods_price")
    private Long goodsPrice;
    @JsonProperty("goods_quantity")
    private Long goodsQuantity;
    @JsonProperty("goods_sign")
    private String goodsSign;
    @JsonProperty("goods_thumbnail_url")
    private String goodsThumbnailUrl;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("is_direct")
    private Integer isDirect;
    @JsonProperty("mall_id")
    private Long mallId;
    @JsonProperty("mall_name")
    private String mallName;
    @JsonProperty("no_subsidy_reason")
    private String noSubsidyReason;
    @JsonProperty("order_amount")
    private Long orderAmount;

    @JsonProperty("order_create_time")
    private Long orderCreateTime;
    @JsonProperty("order_group_success_time")
    private Long orderGroupSuccessTime;
    @JsonProperty("order_modify_at")
    private Long orderModifyAt;
    @JsonProperty("order_pay_time")
    private Long orderPayTime;
    @JsonProperty("order_receive_time")
    private Long orderReceiveTime;
    @JsonProperty("order_settle_time")
    private Long orderSettleTime;
    @JsonProperty("order_sn")
    private String orderSn;
    @JsonProperty("order_status")
    private Integer orderStatus;
    @JsonProperty("order_status_desc")
    private String orderStatusDesc;
    @JsonProperty("order_verify_time")
    private Long orderVerifyTime;
    @JsonProperty("p_id")
    private String pId;
    @JsonProperty("platform_discount")
    private Long platformDiscount;
    @JsonProperty("price_compare_status")
    private Integer priceCompareStatus;
    @JsonProperty("promotion_amount")
    private Long promotionAmount;
    @JsonProperty("promotion_rate")
    private Long promotionRate;
    @JsonProperty("red_packet_type")
    private Integer redPacketType;
    @JsonProperty("sep_duo_id")
    private Long sepDuoId;
    @JsonProperty("sep_market_fee")
    private Integer sepMarketFee;
    @JsonProperty("sep_parameters")
    private String sepParameters;
    @JsonProperty("sep_pid")
    private String sepPid;
    @JsonProperty("sep_rate")
    private Integer sepRate;
    @JsonProperty("share_amount")
    private Integer shareAmount;
    @JsonProperty("share_rate")
    private Integer shareRate;
    @JsonProperty("subsidy_amount")
    private Integer subsidyAmount;
    @JsonProperty("subsidy_duo_amount_level")
    private Integer subsidyDuoAmountLevel;
    @JsonProperty("subsidy_duo_amount_ten_million")
    private Integer subsidyDuoAmountTenMillion;
    @JsonProperty("subsidy_order_remark")
    private String subsidyOrderRemark;
    @JsonProperty("subsidy_type")
    private Integer subsidyType;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("zs_duo_id")
    private Long zsDuoId;

}
