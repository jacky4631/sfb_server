package com.mailvor.modules.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单操作记录表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单操作记录表查询参数")
public class MwStoreOrderStatusQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
