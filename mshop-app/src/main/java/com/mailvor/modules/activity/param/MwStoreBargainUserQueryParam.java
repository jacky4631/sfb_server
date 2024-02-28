package com.mailvor.modules.activity.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

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
@ApiModel(value="用户参与砍价表查询参数", description="用户参与砍价表查询参数")
public class MwStoreBargainUserQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "参数错误")
    @ApiModelProperty(value = "砍价用户ID")
    private String bargainUserUid;

    @ApiModelProperty(value = "砍价产品ID")
    private String bargainId;

}
