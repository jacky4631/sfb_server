package com.mailvor.modules.product.domain;


import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 商品点赞和收藏表
 * </p>
 *
 * @author huangyu
 * @since 2019-10-23
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MwStoreProductRelation extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "商品ID")
    private String productId;

    @ApiModelProperty(value = "淘宝完整商品ID")
    private String originalProductId;

    @ApiModelProperty(value = "类型(收藏(collect）、点赞(like)、足迹(foot))")
    private String type;

    @ApiModelProperty(value = "商品分类,tb jd pdd vip dy")
    private String category;

    @ApiModelProperty(value = "商品图片地址")
    private String img;
    @ApiModelProperty(value = "商品标题")
    private String title;
    @ApiModelProperty(value = "原始价格")
    private String startPrice;
    @ApiModelProperty(value = "真实价格")
    private String endPrice;


}
