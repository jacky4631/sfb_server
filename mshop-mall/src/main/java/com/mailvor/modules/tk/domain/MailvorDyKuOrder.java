/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
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
                .settleTime((earningTime != null && earningTime > 0) ? new Date(earningTime*1000) : null)
                .productName(itemTitle)
                .totalPayAmount(payPrice)
                .productImg(itemImg)
                .orderId(tradeId)
                .shopName(shopName)
                .flowPoint(covertOrderStatus(orderStatus))
                .paySuccessTime(new Date(paidTime*1000))
                .refundTime((refundTime != null && refundTime > 0) ? new Date(refundTime*1000) : null)
                .productId(productId)
                .estimatedTotalCommission(predictMoney)
                .realCommission(actualMoney)
                .itemNum(itemNum)
                //773026_8_0200 中间的是用户id
                .externalInfo("773026_" + channelCode + "_0200")
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

    public static void main(String[] args) {
        String json = "{\"earning_time\":\"0\",\"order_status\":1,\"shop_name\":\"\",\"item_title\":\"【甜蜜一夏】珍珠奶茶\",\"item_img\":\"https://p3-sign.douyinpic.com/tos-cn-i-hf2m9xxmck/00d05c8e7626446387c75667f7643f4c~tplv-shrink:750:0.image?x-expires=1814241600&x-signature=VF%2Bx2p%2BzjSCUiDC5mKy32g273OI%3D&from=3158402634\",\"paid_time\":\"1719138887\",\"trade_id\":\"800008633904836173013291515\",\"trade_parent_id\":\"1061485716822741515\",\"create_time\":\"1719138887\",\"settled_status\":\"0\",\"pay_price\":\"5.58\",\"predict_money\":\"0.06\",\"actual_money\":\"0.00\",\"channel_code\":\"6\",\"update_time\":\"1719138892\",\"is_receipt\":0,\"refund_time\":\"\",\"shop_id\":\"\",\"item_num\":\"\",\"product_id\":\"\"}";
        MailvorDyKuOrder kuOrder = JSON.parseObject(json, MailvorDyKuOrder.class);
        MailvorDyOrder dyOrder = kuOrder.convert();
        System.out.println(dyOrder);
    }
}
