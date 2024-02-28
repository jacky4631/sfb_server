package com.mailvor.modules.shop.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value="MwSystemStoreStaffQueryParam对象", description="门店店员表查询参数")
public class MwSystemStoreStaffQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
