package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 签到记录表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwUserSignQueryParam对象", description="签到记录表查询参数")
public class MwUserSignQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
