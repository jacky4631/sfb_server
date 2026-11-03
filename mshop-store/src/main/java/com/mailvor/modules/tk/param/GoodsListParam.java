/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *
 * @author shenji
 * @date 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品表查询参数")
public class GoodsListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "类别")
    private String type;

    @Schema(description = "分类ID")
    private String sid;

    @Schema(description = "是否新品")
    private String news;

    @Schema(description = "是否积分兑换商品")
    private Integer isIntegral;

    @Schema(description = "价格排序")
    private String priceOrder;

    @Schema(description = "销量排序")
    private String salesOrder;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "最低佣金比率")
    private String commissionRateLowerLimit;

    @Schema(description = "价格（券后价）下限")
    private String priceLowerLimit;

    @Schema(description = "价格（券后价）上限")
    private String priceUpperLimit;


    @Schema(description = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String cids;

    @Schema(description = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String subcid;

    @Schema(description = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private String sort;


    @Schema(description = "1-30天最低价，0-不限，不填默认为0（新品与最低价不能同时选，否则无商品）")
    private Integer lowestPrice;
    @Schema(description = "1-品牌商品，0-所有商品，不填默认为0")
    private Integer brand;
    @Schema(description = "1-天猫超市商品， 0-所有商品，不填默认为0")
    private Integer tchaoshi;

    @Schema(description = "最低优惠券面额")
    private String couponPriceLowerLimit;
    @Schema(description = "最高优惠券面额")
    private String maxCouponAmount;


}
