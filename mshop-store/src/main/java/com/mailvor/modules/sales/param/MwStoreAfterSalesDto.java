/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.sales.param;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author gzlv
* @date 2021-06-30
*/
@Data
public class MwStoreAfterSalesDto implements Serializable {

    private Long id;

    /** 订单号 */
    private String orderCode;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 服务类型0仅退款1退货退款 */
    private Integer serviceType;

    /** 申请原因 */
    private String reasons;

    /** 说明 */
    private String explains;

    /** 说明图片->多个用逗号分割 */
    private String explainImg;

    /** 物流公司编码 */
    private String shipperCode;

    /** 物流单号 */
    private String deliverySn;

    /** 物流名称 */
    private String deliveryName;

    /** 状态 0已提交等待平台审核 1平台已审核 等待用户发货/退款 2 用户已发货 3退款成功 */
    private Integer state;

    /** 售后状态-0正常1用户取消2商家拒绝 */
    private Integer salesState;

    /** 添加时间 */
    private Date createTime;

    /** 逻辑删除 */
    private Integer isDel;

    /** 用户id */
    private Long userId;

    /** 商家收货人 */
    private String consignee;

    /** 商家手机号 */
    private String phoneNumber;

    /** 商家地址 */
    private String address;
}
