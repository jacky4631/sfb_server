/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
* @author shenji
* @date 2022-08-29
*/
@Data
public class MailvorTbOrderDto extends BaseOrderDto{

    /** 订单编号 */
    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long tradeParentId;

    private Long parentId;

    /** 订单创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date tkCreateTime;

    /** 付款时间 */
    private Date tbPaidTime;

    /** 付款时间 */
    private Date tkPaidTime;

    private String payPrice;

    private String pubShareFee;

    /** 子订单编号 */
    private String tradeId;

    private String pubShareRate;

    /** 维权标签，0 含义为非维权 1 含义为维权订单 */
    private Integer refundTag;

    /** 实际获得收益的比率 */
    private Double tkTotalRate;

    /** 付款预估收入 */
    private Double pubSharePreFee;

    /** 买家拍下付款的金额 */
    private Double alipayTotalPrice;

    /** 佣金比率 */
    private Double totalCommissionRate;

    /** 商品标题 */
    private String itemTitle;

    /** 推广位名称 */
    private String adzoneName;

    /** pid=mm_1_2_3中的“2”这段数字 */
    private Long siteId;

    /** pid=mm_1_2_3中的“3”这段数字 */
    private Long adzoneId;

    /** 商品链接 */
    private String itemLink;

    /** 商品单价 */
    private Double itemPrice;

    /** 订单所属平台类型，包括天猫、淘宝、聚划算等 */
    private String orderType;

    /** 店铺名 */
    private String sellerShopTitle;

    /** 媒体名 */
    private String siteName;

    /** 1）买家超时未付款； 2）买家付款前，买家/卖家取消了订单；3）订单付款后发起售中退款成功；3：订单结算，12：订单付款， 13：订单失效，14：订单成功 */
    private Integer tkStatus;

    /** 商品id */
    private String itemId;

    /** 佣金金额=结算金额＊佣金比率 */
    private Double totalCommissionFee;

    /** 会员运营id */
    private Long specialId;

    /** 渠道关系id */
    private Long relationId;

    /** 订单更新时间 */
    private String modifiedTime;

    private String itemImg;

    private Boolean isTlj;

}
