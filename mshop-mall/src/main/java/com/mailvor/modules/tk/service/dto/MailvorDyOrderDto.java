/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;

/**
* @author shenji
* @date 2022-09-07
*/
@Data
public class MailvorDyOrderDto extends BaseOrderDto {

    /** 订单号 */
    private String orderId;

    /** 订单金额 */
    private Double totalPayAmount;

    /** 预估佣金 */
    private Double estimatedTotalCommission;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    /** 下单时间 */
    private Date paySuccessTime;

    /** 商品标题 */
    private String productName;

    /** 商品id */
    private String productId;

    /** 佣金比例 */
    private Integer commissionRate;

    /** 商品主图 */
    private String productImg;

    /** 订单状态，PAY_SUCC */
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
}
