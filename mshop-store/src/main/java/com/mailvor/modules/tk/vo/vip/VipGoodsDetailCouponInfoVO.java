package com.mailvor.modules.tk.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "商品通用vo")
@Data
public class VipGoodsDetailCouponInfoVO {
    @Schema(description = "优惠券金额")
    private Double fav;
}
