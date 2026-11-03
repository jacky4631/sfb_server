package com.mailvor.modules.user.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-16
 */
@Data
@Schema(description = "用户表查询参数")
public class MwUserVipQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "等级")
    private Integer level;
    private Integer levelPdd;
    private Integer levelJd;
    private Integer levelDy;
    private Integer levelVip;

    @Schema(description = "等级名")
    private String levelName;
    private String levelNamePdd;
    private String levelNameJd;
    private String levelNameDy;
    private String levelNameVip;

    @Schema(description = "佣金比例")
    private Double discount;
    private Double discountPdd;
    private Double discountJd;
    private Double discountDy;
    private Double discountVip;

}
