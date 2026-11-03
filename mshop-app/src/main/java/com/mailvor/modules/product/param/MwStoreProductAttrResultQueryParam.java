package com.mailvor.modules.product.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品属性详情表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品属性详情表查询参数")
public class MwStoreProductAttrResultQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
