package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户地址表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwUserAddressQueryParam对象", description="用户地址表查询参数")
public class MwUserAddressQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    private Integer uid;
    private Integer isDel;
}
