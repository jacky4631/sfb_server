package com.mailvor.modules.activity.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>
 * 用户参与砍价表
 * </p>
 *
 * @author huangyu
 * @since 2019-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户参与砍价表")
public class MwStoreBargainUser extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户参与砍价表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "砍价产品id")
    private Long bargainId;

    @Schema(description = "砍价的最低价")
    private BigDecimal bargainPriceMin;

    @Schema(description = "砍价金额")
    private BigDecimal bargainPrice;

    @Schema(description = "砍掉的价格")
    private BigDecimal price;

    @Schema(description = "状态 1参与中 2 活动结束参与失败 3活动结束参与成功")
    private Integer status;


}
