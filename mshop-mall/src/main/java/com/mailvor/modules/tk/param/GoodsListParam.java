/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value="MwGoodQueryParam对象", description="商品表查询参数")
public class GoodsListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别")
    private String type;

    @ApiModelProperty(value = "分类ID")
    private String sid;

    @ApiModelProperty(value = "是否新品")
    private String news;

    @ApiModelProperty(value = "是否积分兑换商品")
    private Integer isIntegral;

    @ApiModelProperty(value = "价格排序")
    private String priceOrder;

    @ApiModelProperty(value = "销量排序")
    private String salesOrder;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "最低佣金比率")
    private String commissionRateLowerLimit;

    @ApiModelProperty(value = "价格（券后价）下限")
    private String priceLowerLimit;

    @ApiModelProperty(value = "价格（券后价）上限")
    private String priceUpperLimit;


    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String cids;

    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String subcid;

    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private String sort;


    @ApiModelProperty(value = "1-30天最低价，0-不限，不填默认为0（新品与最低价不能同时选，否则无商品）")
    private Integer lowestPrice;
    @ApiModelProperty(value = "1-品牌商品，0-所有商品，不填默认为0")
    private Integer brand;
    @ApiModelProperty(value = "1-天猫超市商品， 0-所有商品，不填默认为0")
    private Integer tchaoshi;
}
