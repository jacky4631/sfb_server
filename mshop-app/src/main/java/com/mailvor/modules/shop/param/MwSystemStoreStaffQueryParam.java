package com.mailvor.modules.shop.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 门店店员表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2020-03-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "门店店员表查询参数")
public class MwSystemStoreStaffQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
