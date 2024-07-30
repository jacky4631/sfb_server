package com.mailvor.modules.energy.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 用户热度扶持记录，每天生成一条，生成时判断用户今天是否已经生成
 * </p>
 *
 * @author huangyu
 * @since 2023-2-4
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@ToString
@TableName(value = "mw_user_energy_order_log",autoResultMap = true)
public class UserEnergyOrderLog extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "热度记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "提成金额")
    private BigDecimal money;

    @ApiModelProperty(value = "订单数量")
    private Integer count;
    @ApiModelProperty(value = "用户当前热度值")
    private BigDecimal energy;

    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;
    @ApiModelProperty(value = "类型 0=赠送 1=推广")
    private Integer type;
}
