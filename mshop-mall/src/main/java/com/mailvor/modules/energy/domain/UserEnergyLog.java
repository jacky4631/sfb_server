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
 * 用户热度增加记录
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
@TableName(value = "mw_user_energy_log",autoResultMap = true)
public class UserEnergyLog extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "热度日志id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "获取热度的用户id")
    private Long uid;

    @ApiModelProperty(value = "发起热度的用户id")
    private Long oid;

    @ApiModelProperty(value = "增加/减少热度")
    private BigDecimal energy;

    @ApiModelProperty(value = "当前热度")
    private BigDecimal totalEnergy;

    @ApiModelProperty(value = "增加=1 减少=2")
    private Integer type;

    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;

}
