package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 等级任务设置 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwSystemUserTaskQueryParam对象", description="等级任务设置查询参数")
public class MwSystemUserTaskQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
