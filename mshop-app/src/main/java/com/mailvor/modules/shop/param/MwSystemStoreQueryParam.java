package com.mailvor.modules.shop.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 门店自提 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2020-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "门店自提查询参数")
public class MwSystemStoreQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "经度")
    private String longitude;

}
