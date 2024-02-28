package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value="MwUserQueryParam对象", description="用户表查询参数")
public class MwUserQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
