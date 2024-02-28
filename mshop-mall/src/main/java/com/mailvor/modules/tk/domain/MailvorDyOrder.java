/**
 * Copyright (C) 2018-2023
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* @author shenji
* @date 2022-09-07
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("mailvor_dy_order")
public class MailvorDyOrder extends TkOrder {
    /** 订单号 */
    @TableId
    private String orderId;

    /** 订单金额 */
    private Double totalPayAmount;

    /** 预估佣金 */
    private Double estimatedTotalCommission;

    /** 下单时间 */
    @NotNull
    private Date paySuccessTime;

    /** 商品标题 */
    private String productName;

    /** 商品id */
    private String productId;

    /** 佣金比例 */
    private Double commissionRate;

    /** 商品主图 */
    private String productImg;

    /** 订单状态，PAY_SUCC */
    @NotBlank
    private String flowPoint;

    /** 退款时间 */
    private Date refundTime;

    /** 结算时间 */
    private Date settleTime;

    /** 商品数量 */
    private Integer itemNum;

    /** 店铺名称 */
    private String shopName;

    /** 店铺id */
    private Integer shopId;

    /** 实际结算金额 */
    private Double realCommission;

    /** 媒体名称 */
    private String mediaTypeName;

    /** 媒体类型 */
    private String mediaType;

    private Double estimatedCommission;

    private Double estimatedTechServiceFee;

    private Double estimatedTotalCommission0;

    private String app;
    private Integer adsEstimatedCommission;
    private Integer adsRealCommission;
    private String externalInfo;
    private Double payGoodsAmount;
    private Double settledGoodsAmount;

    public void copy(MailvorDyOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
