package com.mailvor.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.product.service.dto.AttrValueDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品属性表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-23
 */
@Data
@Schema(description = "商品属性表查询参数")
public class MwStoreProductAttrQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "属性名")
    private String attrName;

    @Schema(description = "属性值")
    private String attrValues;

    @Schema(description = "属性值集合")
    private List<AttrValueDto> attrValue;

    @Schema(description = "属性")
    private List<String> attrValueArr;

}
