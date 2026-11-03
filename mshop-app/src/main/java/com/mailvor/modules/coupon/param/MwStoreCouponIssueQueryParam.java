package com.mailvor.modules.coupon.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 优惠券前台领取表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "优惠券前台领取表查询参数")
public class MwStoreCouponIssueQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
