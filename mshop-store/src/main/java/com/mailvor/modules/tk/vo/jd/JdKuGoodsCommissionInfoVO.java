/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "商品通用vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdKuGoodsCommissionInfoVO {
    /**
     * {
     *   "data" : [ {
     *     "commission_info" : {
     *       "isLock" : 0,
     *       "commissionShare" : 2,
     *       "plusCommissionShare" : 2,
     *       "commission" : 0.03,
     *       "startTime" : 1731340800000,
     *       "couponCommission" : 0.03,
     *       "endTime" : 2147529599000
     *     },
     *   } ]
     * }
     * */

    @JSONField(name = "commission")
    @Schema(description = "佣金")
    private Double fee;
    @JSONField(name = "commissionShare")
    @Schema(description = "佣金比例")
    private Double feeRatio;

}
