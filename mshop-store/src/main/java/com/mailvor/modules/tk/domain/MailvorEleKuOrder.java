/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(name = "trade_id")
    private String tradeId;
    /** 父订单号 */
    @JSONField(name = "trade_parent_id")
    private String tradeParentId;

    /** 下单时间 */
    @JSONField(name = "paid_time")
    private Long paidTime;

    /** 下单时间 */
    @JSONField(name = "create_time")
    private Long createTime;

    /** 订单金额 */
    @JSONField(name = "pay_price")
    private Double payPrice;


    /** 商品标题 */
    @JSONField(name = "item_title")
    private String itemTitle;
    /** 商品主图 */
    @JSONField(name = "item_img")
    private String itemImg;


    /** 订单状态：1已付款、2已结算、3已退款、4已收货
     */
    @JSONField(name = "order_status")
    private Integer orderStatus;

    /** 结算状态：0待结算、1已结算、2订单无效
     */
    @JSONField(name = "settled_status")
    private Integer settledStatus;
    /** 结算时间 */
    @JSONField(name = "earning_time")
    private Long earningTime;
    /** 店铺名称 */
    @JSONField(name = "shop_name")
    private String shopName;

    /** 渠道信息 */
    @JSONField(name = "channel_code")
    private String channelCode;
    /** 预估佣金*/
    @JSONField(name = "predict_money")
    private Double predictMoney;


    /** 实际佣金 */
    @JSONField(name = "actual_money")
    private Double actualMoney;

    /** 店铺ID */
    @JSONField(name = "shop_id")
    private Integer shopId;


    /** 订单更新时间*/
    @JSONField(name = "update_time")
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
