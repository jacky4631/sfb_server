/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-08-11
*/
@Data
public class MwWechatLiveGoodsDto implements Serializable {


    private Long id;

    /** 直播商品id */
    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long goodsId;

    /** 关联商品id */
    private Long productId;

    /** 商品图片 */
    private String coverImgeUrl;

    /** 商品小程序路径 */
    private String url;

    /** 价格类型 1：一口价（只需要传入price，price2不传） 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传） 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传） */
    private String priceType;

    private String price;

    private String price2;

    /** 商品名称 */
    private String name;

    /** 1, 2：表示是为api添加商品，否则是直播控制台添加的商品 */
    private String thirdPartyTag;

    /** 审核单id */
    private Long auditId;

    /** 审核状态 0：未审核，1：审核中，2:审核通过，3审核失败 */
    private Integer auditStatus;
}
