package com.mailvor.modules.product.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "淘宝完整商品ID")
    private String originalProductId;

    @Schema(description = "类型(收藏(collect）、点赞(like)、足迹(foot))")
    private String type;

    @Schema(description = "商品分类,tb jd pdd vip dy")
    private String category;

    @Schema(description = "商品图片地址")
    private String img;
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "原始价格")
    private String startPrice;
    @Schema(description = "真实价格")
    private String endPrice;


}
