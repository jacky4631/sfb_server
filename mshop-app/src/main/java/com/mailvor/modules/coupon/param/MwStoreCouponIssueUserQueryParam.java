package com.mailvor.modules.coupon.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 优惠券前台用户领取记录表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwStoreCouponIssueUserQueryParam对象", description="优惠券前台用户领取记录表查询参数")
public class MwStoreCouponIssueUserQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
