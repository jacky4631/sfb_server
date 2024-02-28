package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户任务完成记录表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwUserTaskFinishQueryParam对象", description="用户任务完成记录表查询参数")
public class MwUserTaskFinishQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
