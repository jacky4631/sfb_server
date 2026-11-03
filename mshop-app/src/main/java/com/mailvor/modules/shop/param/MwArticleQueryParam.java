package com.mailvor.modules.shop.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 文章管理表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章管理表查询参数")
public class MwArticleQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
