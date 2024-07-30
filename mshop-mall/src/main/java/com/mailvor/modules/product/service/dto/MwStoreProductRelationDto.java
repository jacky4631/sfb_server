/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.dto;

import com.mailvor.modules.product.domain.MwStoreProduct;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.io.Serializable;

/**
 * @author huangyu
 * @date 2020-09-03
 */
@Data
public class MwStoreProductRelationDto implements Serializable {

    private Long id;

    /** 用户ID */
    private Long uid;

    private String userName;

    /** 商品ID */
    private String productId;
    private String originalProductId;

    private MwStoreProduct product;

    /** 类型(收藏(collect）、点赞(like)) */
    private String type;

    /** 商品分类,tb jd pdd vip dy */
    private String category;

    private String img;
    private String title;
    private String startPrice;
    private String endPrice;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    private Integer isDel;
}

