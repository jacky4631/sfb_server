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
@ApiModel(value="GoodsListPddParam", description="商品表查询参数")
public class GoodsListPddParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "排序方式:0-综合排序;2-按佣金比例降序;3-按价格升序;4-按价格降序;6-按销量降序;9-券后价升序排序;10-券后价降序排序;16-店铺描述评分降序")
    private Integer sortType;

    @ApiModelProperty(value = "是否只返回优惠券的商品，0返回所有商品，1只返回有优惠券的商品")
    private Integer withCoupon;

    @ApiModelProperty(value = "翻页时建议填写前页返回的list_id值")
    private String listId;

    @ApiModelProperty(value = "catId")
    private Integer catId;

    @ApiModelProperty(value = "默认值1，商品分页数")
    private Integer page = 1;

}
