package com.mailvor.modules.coupon.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * <p>
 * 优惠券表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "优惠券表查询参数")
public class MwStoreCouponQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "参数有误")
    @Schema(description = "优惠券ID")
    private String couponId;
}
