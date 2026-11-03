package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户表查询参数")
public class MwUserQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
