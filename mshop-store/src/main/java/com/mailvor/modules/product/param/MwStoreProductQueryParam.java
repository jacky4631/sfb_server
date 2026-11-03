package com.mailvor.modules.product.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品表查询参数")
public class MwStoreProductQueryParam extends QueryParam {
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
}
