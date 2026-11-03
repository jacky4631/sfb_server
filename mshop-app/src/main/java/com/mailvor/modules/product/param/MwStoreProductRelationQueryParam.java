package com.mailvor.modules.product.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

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
@Schema(description = "查询参数对象")
public class MwStoreProductRelationQueryParam {

    @NotBlank(message = "参数有误")
    @Schema(description = "商品id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "淘宝商品id，只有category为tb时存在")
    private String originalId;

    @Schema(description = "商品分类,tb jd pdd vip dy")
    private String category = "tb";

    @Schema(description = "商品图片地址")
    private String img;
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "原始价格")
    private String startPrice;
    @Schema(description = "真实价格")
    private String endPrice;
}
