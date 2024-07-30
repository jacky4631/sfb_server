/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 *
 *
 * @author shenji
 * @date 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="GoodsListJDParam对象", description="京东商品表查询参数")
public class GoodsListJDParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "一级类目id")
    private String cid1;

    @ApiModelProperty(value = "二级类目id")
    private String cid2;

    @ApiModelProperty(value = "三级类目id")
    private String cid3;

    @ApiModelProperty(value = "页码")
    private Integer pageId;

    @ApiModelProperty(value = "价格排序")
    private Integer pageSize;

    @ApiModelProperty(value = "销量排序")
    private String skuIds;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "最低佣金比率")
    private BigDecimal priceFrom;

    @ApiModelProperty(value = "价格（券后价）下限")
    private BigDecimal priceTo;

    @ApiModelProperty(value = "佣金比例区间开始")
    private Integer commissionShareStart;


    @ApiModelProperty(value = "佣金比例区间结束")
    private Integer commissionShareEnd;

    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String owner;

    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private String sortName;


    @ApiModelProperty(value = "价格（券后价）上限")
    private String sort;


    @ApiModelProperty(value = "是否是优惠券商品，1：有优惠券，0：无优惠券")
    private Integer isCoupon;

    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private BigDecimal pingouPriceStart;

    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private BigDecimal pingouPriceEnd;


    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String brandCode;

    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer shopId;

    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private String hasBestCoupon;

    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String pid;

    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String jxFlags;

    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private Long spuId;

    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String couponUrl;

    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer deliveryType;

}
