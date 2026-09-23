package com.dtk.api.response.mastertool;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单查询接口响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:31
 */
@Getter
@Setter
public class DtkGetOrderDetailsResultsItemsResponse {
    @JsonAlias("terminal_type")
    private String terminalType;
    @JsonAlias("item_category_name")
    private String itemCategoryName;
    @JsonAlias("tk_paid_time")
    private String tkPaidTime;
    @JsonAlias("adzone_id")
    private Long adzoneId;
    @JsonAlias("alipay_total_price")
    private String alipayTotalPrice;
    @JsonAlias("seller_shop_title")
    private String sellerShopTitle;
    @JsonAlias("alimama_share_fee")
    private String alimamaShareFee;
    @JsonAlias("trade_id")
    private String tradeId;
    @JsonAlias("click_time")
    private String clickTime;
    @JsonAlias("subsidy_rate")
    private String subsidyRate;
    @JsonAlias("refund_tag")
    private Integer refundTag;
    @JsonAlias("item_title")
    private String itemTitle;
    @JsonAlias("order_type")
    private String orderType;
    @JsonAlias("tb_paid_time")
    private String tbPaidTime;
    @JsonAlias("tk_order_role")
    private Integer tkOrderRole;
    @JsonAlias("total_commission_fee")
    private String totalCommissionFee;
    @JsonAlias("pub_id")
    private Long pubId;
    @JsonAlias("pay_price")
    private String payPrice;
    @JsonAlias("item_img")
    private String itemImg;
    @JsonAlias("alimama_rate")
    private String alimamaRate;
    @JsonAlias("item_id")
//    private Long itemId;
    private String itemId;
    @JsonAlias("item_price")
    private String itemPrice;
    @JsonAlias("tb_deposit_time")
    private String tbDepositTime;
    @JsonAlias("tk_status")
    private Integer tkStatus;
    @JsonAlias("total_commission_rate")
    private String totalCommissionRate;
    @JsonAlias("trade_parent_id")
    private String tradeParentId;
    @JsonAlias("subsidy_type")
    private String subsidyType;
    @JsonAlias("tk_create_time")
    private String tkCreateTime;
    @JsonAlias("pub_share_fee")
    private String pubShareFee;
    @JsonAlias("item_num")
    private Integer itemNum;
    @JsonAlias("tk_commission_fee_for_media_platform")
    private String tkCommissionFeeForMediaPlatform;
    @JsonAlias("income_rate")
    private String incomeRate;
    @JsonAlias("site_name")
    private String siteName;
    @JsonAlias("pub_share_pre_fee")
    private String pubSharePreFee;
    @JsonAlias("tk_commission_rate_for_media_platform")
    private String tkCommissionRateForMediaPlatform;
    @JsonAlias("tk_deposit_time")
    private String tkDepositTime;
    @JsonAlias("tk_total_rate")
    private String tkTotalRate;
    @JsonAlias("pub_share_rate")
    private String pubShareRate;
    @JsonAlias("adzone_name")
    private String adzoneName;
    @JsonAlias("site_id")
    private Long siteId;
    @JsonAlias("item_link")
    private String itemLink;
    @JsonAlias("deposit_price")
    private String depositPrice;
    @JsonAlias("is_lx")
    private String isLx;
    @JsonAlias("seller_nick")
    private String sellerNick;
    @JsonAlias("subsidy_fee")
    private String subsidyFee;
    @JsonAlias("flow_source")
    private String flowSource;
    @JsonAlias("tk_commission_pre_fee_for_media_platform")
    private String tkCommissionPreFeeForMediaPlatform;
    @JsonAlias("tk_earning_time")
    private String tkEarningTime;
    @JsonAlias("special_id")
    private String specialId;
    @JsonAlias("relation_id")
    private String relationId;
    @JsonAlias("modified_time")
    private String modifiedTime;

    @JsonAlias("app_key")
    private String appKey;

    @JsonAlias("alsc_id")
    private String alscId;

    @JsonAlias("alsc_pid")
    private String alscPid;

    @JsonAlias("service_fee_dto_list")
    private List<ServiceFeeDto> serviceFeeDtoList;

    @JsonAlias("tk_share_role_type")
    private BigDecimal tkShareRoleType;

    @Getter
    @Setter
    public static class ServiceFeeDto {
        @JsonAlias("share_relative_rate")
        private String shareRelativeRate;

        @JsonAlias("share_fee")
        private String shareFee;

        @JsonAlias("share_pre_fee")
        private String sharePreFee;

        @JsonAlias("tk_share_role_type")
        private String tkShareRoleType;
    }
}
