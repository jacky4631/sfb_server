package com.mailvor.modules.energy.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 用户热度
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
@TableName(value = "mw_user_energy",autoResultMap = true)
public class UserEnergy extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(value = "用户id")
    private Long uid;
    @ApiModelProperty(value = "淘宝热度")
    private BigDecimal tbEnergy;
    @ApiModelProperty(value = "京东热度")
    private BigDecimal jdEnergy;
    @ApiModelProperty(value = "拼多多热度")
    private BigDecimal pddEnergy;
    @ApiModelProperty(value = "抖音热度")
    private BigDecimal dyEnergy;
    @ApiModelProperty(value = "唯品会热度")
    private BigDecimal vipEnergy;

    @ApiModelProperty(value = "淘推热度")
    private BigDecimal tbTuiEnergy;
    @ApiModelProperty(value = "京推热度")
    private BigDecimal jdTuiEnergy;
    @ApiModelProperty(value = "多推热度")
    private BigDecimal pddTuiEnergy;
    @ApiModelProperty(value = "抖推热度")
    private BigDecimal dyTuiEnergy;
    @ApiModelProperty(value = "唯推热度")
    private BigDecimal vipTuiEnergy;
    @ApiModelProperty(value = "淘每日热度")
    private BigDecimal tbDay;
    @ApiModelProperty(value = "京每日热度")
    private BigDecimal jdDay;
    @ApiModelProperty(value = "多每日热度")
    private BigDecimal pddDay;
    @ApiModelProperty(value = "抖每日热度")
    private BigDecimal dyDay;
    @ApiModelProperty(value = "唯每日热度")
    private BigDecimal vipDay;
}
