package com.mailvor.modules.energy.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 用户热度
 * </p>
 *
 * @author huangyu
 * @since 2023-2-4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEnergyDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private Long uid;
    @ApiModelProperty(value = "淘宝热度")
    private BigDecimal tbEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "京东热度")
    private BigDecimal jdEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "拼多多热度")
    private BigDecimal pddEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "抖音热度")
    private BigDecimal dyEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "唯品会热度")
    private BigDecimal vipEnergy = BigDecimal.ZERO;

    @ApiModelProperty(value = "淘宝推广热度")
    private BigDecimal tbTuiEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "京东推广热度")
    private BigDecimal jdTuiEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "拼多多推广热度")
    private BigDecimal pddTuiEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "抖音推广热度")
    private BigDecimal dyTuiEnergy = BigDecimal.ZERO;
    @ApiModelProperty(value = "唯品会推广热度")
    private BigDecimal vipTuiEnergy = BigDecimal.ZERO;

    @ApiModelProperty(value = "总热度")
    private BigDecimal totalEnergy = BigDecimal.ZERO;

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
    @ApiModelProperty(value = "默认每日消耗热度")
    private BigDecimal defaultDayEnergy;

    @ApiModelProperty(value = "最大每日消耗热度")
    private BigDecimal dayEnergyMax;
}
