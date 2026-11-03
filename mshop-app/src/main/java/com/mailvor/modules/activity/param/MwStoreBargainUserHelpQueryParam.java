package com.mailvor.modules.activity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * <p>
 * 砍价用户帮助表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "砍价用户帮助表查询参数")
public class MwStoreBargainUserHelpQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "参数有误")
    @Schema(description = "砍价产品ID")
    private String bargainId;

    @NotBlank(message = "参数有误")
    @Schema(description = "砍价用户id")
    private String bargainUserUid;
}
