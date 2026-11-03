package com.mailvor.modules.manage.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 快递公司表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "快递公司表查询参数")
public class MwExpressQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
