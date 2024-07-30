/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

/**
* @author shenji
* @date 2024-3-07
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value="mw_ele_order", autoResultMap = true)
public class MailvorEleOrder extends TkOrder {
    /** 子订单号 */
    private String tradeId;
    /** 父订单号 */
    private String tradeParentId;

    /** 下单时间 */
    private Date paidTime;

    /** 下单时间 */
    private Date createTime;

    /** 订单金额 */
    private Double payPrice;


    /** 商品标题 */
    private String itemTitle;
    /** 商品主图 */
    private String itemImg;


    /** 订单状态：1已付款、2已结算、3已退款、4已收货
     */
    private Integer orderStatus;

    /** 结算状态：0待结算、1已结算、2订单无效
     */
    private Integer settledStatus;
    /** 结算时间 */
    private Date earningTime;
    /** 店铺名称 */
    private String shopName;

    /** 渠道信息 */
    private String channelCode;
    /** 预估佣金*/
    private Double predictMoney;


    /** 实际佣金 */
    private Double actualMoney;

    /** 店铺ID */
    private Integer shopId;


    /** 订单更新时间*/
    private Date updateTime;


    public void copy(MailvorDyOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
