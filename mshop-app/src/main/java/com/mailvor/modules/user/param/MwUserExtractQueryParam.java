package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户提现表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户提现表查询参数")
public class MwUserExtractQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
