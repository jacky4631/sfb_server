/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.dto;

import com.mailvor.modules.product.domain.MwStoreProduct;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class MwStoreCouponDto implements Serializable {

    // 优惠券表ID
    private Integer id;

    // 优惠券名称
    private String title;

    // 兑换消耗积分值
    private Integer integral;

    // 兑换的优惠券面值
    private BigDecimal couponPrice;

    // 最低消费多少金额可用优惠券
    private BigDecimal useMinPrice;

    // 优惠券有效期限（单位：天）
    private Integer couponTime;

    // 排序
    private Integer sort;

    // 状态（0：关闭，1：开启）
    private Integer status;

    private Integer type;

    private String productId;

    private List<MwStoreProduct> product;

    // 兑换项目添加时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


}
