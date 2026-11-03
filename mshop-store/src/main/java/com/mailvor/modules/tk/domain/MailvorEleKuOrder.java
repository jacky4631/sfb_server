/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @author shenji
* @date 2024-3-07
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailvorEleKuOrder {
    /** 子订单号 */
    @JsonProperty("trade_id")
    private String tradeId;
    /** 父订单号 */
    @JsonProperty("trade_parent_id")
    private String tradeParentId;

    /** 下单时间 */
    @JsonProperty("paid_time")
    private Long paidTime;

    /** 下单时间 */
    @JsonProperty("create_time")
    private Long createTime;

    /** 订单金额 */
    @JsonProperty("pay_price")
    private Double payPrice;


    /** 商品标题 */
    @JsonProperty("item_title")
    private String itemTitle;
    /** 商品主图 */
    @JsonProperty("item_img")
    private String itemImg;


    /** 订单状态：1已付款、2已结算、3已退款、4已收货
     */
    @JsonProperty("order_status")
    private Integer orderStatus;

    /** 结算状态：0待结算、1已结算、2订单无效
     */
    @JsonProperty("settled_status")
    private Integer settledStatus;
    /** 结算时间 */
    @JsonProperty("earning_time")
    private Long earningTime;
    /** 店铺名称 */
    @JsonProperty("shop_name")
    private String shopName;

    /** 渠道信息 */
    @JsonProperty("channel_code")
    private String channelCode;
    /** 预估佣金*/
    @JsonProperty("predict_money")
    private Double predictMoney;


    /** 实际佣金 */
    @JsonProperty("actual_money")
    private Double actualMoney;

    /** 店铺ID */
    @JsonProperty("shop_id")
    private Integer shopId;


    /** 订单更新时间*/
    @JsonProperty("update_time")
    private Long updateTime;


    public MailvorEleOrder convert() {
        return MailvorEleOrder.builder()
                .earningTime(new Date(earningTime*1000))
                .orderStatus(orderStatus)
                .shopName(shopName)
                .itemTitle(itemTitle)
                .itemImg(itemImg)
                .paidTime(new Date(paidTime*1000))
                .tradeId(tradeId)
                .tradeParentId(tradeParentId)
                .createTime(new Date(createTime*1000))
                .settledStatus(settledStatus)
                .payPrice(payPrice)
                .predictMoney(predictMoney)
                .actualMoney(actualMoney)
                .shopId(shopId)
                .channelCode(channelCode)
                .updateTime(new Date(updateTime*1000))
                .build();
    }


}
