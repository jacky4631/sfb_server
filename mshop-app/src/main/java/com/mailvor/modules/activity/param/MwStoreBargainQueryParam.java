package com.mailvor.modules.activity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 砍价表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "砍价表查询参数")
public class MwStoreBargainQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
