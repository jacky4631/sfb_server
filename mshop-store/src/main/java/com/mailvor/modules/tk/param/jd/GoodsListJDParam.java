/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param.jd;

import com.mailvor.modules.tk.param.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description="GoodsListJDParam对象")
public class GoodsListJDParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "一级类目id")
    private String cid1;

    @Schema(description = "二级类目id")
    private String cid2;

    @Schema(description = "三级类目id")
    private String cid3;

    @Schema(description = "销量排序")
    private String skuIds;

    @Schema(description = "最低佣金比率")
    private BigDecimal priceFrom;

    @Schema(description = "价格（券后价）下限")
    private BigDecimal priceTo;

    @Schema(description = "佣金比例区间开始")
    private Integer commissionShareStart;


    @Schema(description = "佣金比例区间结束")
    private Integer commissionShareEnd;

    @Schema(description = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String owner;

    @Schema(description = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到低")
    private String sortName;

    @Schema(description = "asc desc")
    private String sort;


    @Schema(description = "是否是优惠券商品，1：有优惠券，0：无优惠券")
    private Integer isCoupon;

    @Schema(description = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private BigDecimal pingouPriceStart;

    @Schema(description = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private BigDecimal pingouPriceEnd;


    @Schema(description = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String brandCode;

    @Schema(description = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer shopId;

    @Schema(description = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private String hasBestCoupon;

    @Schema(description = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String pid;

    @Schema(description = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String jxFlags;

    @Schema(description = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private Long spuId;

    @Schema(description = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String couponUrl;

    @Schema(description = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer deliveryType;

}
