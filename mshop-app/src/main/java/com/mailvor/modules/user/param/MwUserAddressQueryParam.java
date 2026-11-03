package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
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
@Schema(description = "用户地址表查询参数")
public class MwUserAddressQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
    private Integer uid;
    private Integer isDel;
}
