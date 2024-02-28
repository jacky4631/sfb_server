package com.mailvor.modules.product.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 商品点赞和收藏表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-23
 */
@Getter
@Setter
@ApiModel("查询参数对象")
public class MwStoreProductRelationQueryParam {

    @NotBlank(message = "参数有误")
    @ApiModelProperty(value = "商品id",required=true)
    private String id;

    @ApiModelProperty(value = "淘宝商品id，只有category为tb时存在")
    private String originalId;

    @ApiModelProperty(value = "商品分类,tb jd pdd vip dy")
    private String category = "tb";

    @ApiModelProperty(value = "商品图片地址")
    private String img;
    @ApiModelProperty(value = "商品标题")
    private String title;
    @ApiModelProperty(value = "原始价格")
    private String startPrice;
    @ApiModelProperty(value = "真实价格")
    private String endPrice;
}
