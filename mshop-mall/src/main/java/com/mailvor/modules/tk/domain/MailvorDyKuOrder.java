/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @author shenji
* @date 2023-12-07
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailvorDyKuOrder{
    /** 订单号 */
    @JSONField(name = "trade_id")
    private String tradeId;

    /** 订单金额 */
    @JSONField(name = "pay_price")
    private Double payPrice;

    /** 下单时间 */
    @JSONField(name = "paid_time")
    private Long paidTime;

    /** 商品标题 */
    @JSONField(name = "item_title")
    private String itemTitle;

    /** 商品id */
    @JSONField(name = "product_id")
    private String productId;

    /** 商品主图 */
    @JSONField(name = "item_img")
    private String itemImg;

    /** 订单状态：1已付款、2已结算、3已退款
     */
    @JSONField(name = "order_status")
    private Integer orderStatus;
    /** 结算状态：0待结算、1已结算、2订单无效
     */
    @JSONField(name = "settled_status")
    private Integer settledStatus;

    /** 退款时间 */
    private Integer refundTime;

    /** 结算时间 */
    @JSONField(name = "earning_time")
    private Integer earningTime;

    /** 商品数量 */
    @JSONField(name = "item_num")
    private Integer itemNum;

    /** 店铺名称 */
    @JSONField(name = "shop_name")
    private String shopName;

    /** 实际结算金额 */
    @JSONField(name = "actual_money")
    private Double actualMoney;


    @JSONField(name = "predict_money")
    private Double predictMoney;

    @JSONField(name = "channel_code")
    private String channelCode;

    public MailvorDyOrder convert() {
        return MailvorDyOrder.builder()
                .settleTime(new Date(earningTime*1000))
                .productName(itemTitle)
                .totalPayAmount(payPrice)
                .productImg(itemImg)
                .orderId(tradeId)
                .shopName(shopName)
                .flowPoint(covertOrderStatus(orderStatus))
                .paySuccessTime(new Date(paidTime*1000))
                .refundTime(new Date(refundTime*1000))
                .productId(productId)
                .estimatedTotalCommission(predictMoney)
                .realCommission(actualMoney)
                .itemNum(itemNum)
                .externalInfo(channelCode)
                .commissionRate(NumberUtil.round(NumberUtil.div(actualMoney, payPrice), 2).doubleValue())
                .build();
    }

    private String covertOrderStatus(Integer orderStatus) {
        switch (orderStatus){
            case 1:
                return "PAY_SUCC";
            case 2:
                return "SETTLE";
            default:
                return "REFUND";
        }
    }

}
