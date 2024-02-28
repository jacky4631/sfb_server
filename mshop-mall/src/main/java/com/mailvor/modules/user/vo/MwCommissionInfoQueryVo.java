package com.mailvor.modules.user.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2022-12-16
 */
@Data
@ApiModel(value = "MwCommissionInfoQueryVo", description = "用户表查询参数")
public class MwCommissionInfoQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "淘宝扣除服务费后比例")
    private Integer tbRebateScale;
    @ApiModelProperty(value = "淘宝拆红包比例")
    private Integer hbRebateScale;
    @ApiModelProperty(value = "京店拆红包比例")
    private Integer jdHbRebateScale;
    @ApiModelProperty(value = "拼多多拆红包比例")
    private Integer pddHbRebateScale;
    @ApiModelProperty(value = "抖音拆红包比例")
    private Integer dyHbRebateScale;
    @ApiModelProperty(value = "唯品会拆红包比例")
    private Integer vipHbRebateScale;
    @ApiModelProperty(value = "拆红包倍数最小值")
    private Double hbMinTimes;
    @ApiModelProperty(value = "拆红包倍数最大值")
    private Double hbMaxTimes;

    @ApiModelProperty(value = "淘宝倍数")
    private Double tbTimes;
    private Double jdTimes;
    private Double pddTimes;
    private Double dyTimes;
    private Double vipTimes;
}
