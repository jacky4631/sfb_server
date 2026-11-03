package com.mailvor.modules.shop.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 配置表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "配置表查询参数")
public class MwSystemConfigQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
