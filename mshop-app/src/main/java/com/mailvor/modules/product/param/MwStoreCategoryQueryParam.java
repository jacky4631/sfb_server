package com.mailvor.modules.product.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品分类表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwStoreCategoryQueryParam对象", description="商品分类表查询参数")
public class MwStoreCategoryQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
