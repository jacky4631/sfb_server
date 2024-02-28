package com.mailvor.modules.activity.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品秒杀产品表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="商品秒杀产品表查询参数", description="商品秒杀产品表查询参数")
public class MwStoreSeckillQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
