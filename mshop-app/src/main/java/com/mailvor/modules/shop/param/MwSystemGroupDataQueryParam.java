package com.mailvor.modules.shop.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 组合数据详情表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwSystemGroupDataQueryParam对象", description="组合数据详情表查询参数")
public class MwSystemGroupDataQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
