package com.mailvor.modules.product.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 评论表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwStoreProductReplyQueryParam对象", description="评论表查询参数")
public class MwStoreProductReplyQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
