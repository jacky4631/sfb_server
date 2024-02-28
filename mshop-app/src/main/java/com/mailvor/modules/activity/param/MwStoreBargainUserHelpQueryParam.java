package com.mailvor.modules.activity.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

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
@ApiModel(value="砍价用户帮助表查询参数", description="砍价用户帮助表查询参数")
public class MwStoreBargainUserHelpQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "参数有误")
    @ApiModelProperty(value = "砍价产品ID")
    private String bargainId;

    @NotBlank(message = "参数有误")
    @ApiModelProperty(value = "砍价用户id")
    private String bargainUserUid;
}
