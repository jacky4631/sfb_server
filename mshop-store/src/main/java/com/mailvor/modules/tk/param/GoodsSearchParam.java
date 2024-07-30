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
@ApiModel(value="GoodsSearchParam", description="商品表查询参数")
public class GoodsSearchParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "分类ID")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "是否新品")
    private String keyWords;

    @ApiModelProperty(value = "是否积分兑换商品")
    private String sort;

    @ApiModelProperty(value = "是否积分兑换商品")
    private Integer source;

    @ApiModelProperty(value = "价格排序")
    private Integer overseas;

    @ApiModelProperty(value = "销量排序")
    private Integer endPrice;

    @ApiModelProperty(value = "关键字")
    private Integer startPrice;

    @ApiModelProperty(value = "最低佣金比率")
    private Integer startTkRate;

    @ApiModelProperty(value = "价格（券后价）下限")
    private Integer endTkRate;

    @ApiModelProperty(value = "是否有优惠券")
    private Boolean hasCoupon;


    @ApiModelProperty(value = "会员运营id")
    private String specialId;

    @ApiModelProperty(value = "渠道id将会和传入的pid进行验证，验证通过将正常转链")
    private String channelId;

    @ApiModelProperty(value = "商品所在地，默认为全部商品")
    private String itemLoc;

    @ApiModelProperty(value = "商品是否加入消费者保障，1为加入消费者保障，默认全部")
    private String needPrepay;

    @ApiModelProperty(value = "商品好评率是否高于行业均值，1为高于行业均值，默认全部")
    private String includeGoodRate;

}
