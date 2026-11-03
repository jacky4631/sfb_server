package com.mailvor.modules.activity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * <p>
 * 用户参与砍价表 查询参数对象
 * </p>
 *
 * @author mazhongjun
 * @date 2019-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户参与砍价表查询参数")
public class MwStoreBargainUserQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "参数错误")
    @Schema(description = "砍价用户ID")
    private String bargainUserUid;

    @Schema(description = "砍价产品ID")
    private String bargainId;

}
