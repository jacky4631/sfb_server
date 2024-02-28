package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 设置用户等级表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwSystemUserLevelQueryParam对象", description="设置用户等级表查询参数")
public class MwSystemUserLevelQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
