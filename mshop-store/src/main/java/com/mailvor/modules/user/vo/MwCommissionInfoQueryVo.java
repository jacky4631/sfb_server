package com.mailvor.modules.user.vo;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "用户表查询参数")
public class MwCommissionInfoQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "淘宝扣除服务费后比例")
    private Integer tbRebateScale;
    @Schema(description = "淘宝拆红包比例")
    private Integer hbRebateScale;
    @Schema(description = "京店拆红包比例")
    private Integer jdHbRebateScale;
    @Schema(description = "拼多多拆红包比例")
    private Integer pddHbRebateScale;
    @Schema(description = "抖音拆红包比例")
    private Integer dyHbRebateScale;
    @Schema(description = "唯品会拆红包比例")
    private Integer vipHbRebateScale;
    @Schema(description = "拆红包倍数最小值")
    private Double hbMinTimes;
    @Schema(description = "拆红包倍数最大值")
    private Double hbMaxTimes;

    @Schema(description = "淘宝倍数")
    private Double tbTimes;
    private Double jdTimes;
    private Double pddTimes;
    private Double dyTimes;
    private Double vipTimes;
}
