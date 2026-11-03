package com.mailvor.modules.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户参与砍价表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-21
 */
@Data
@Schema(description = "用户参与砍价表查询参数")
public class MwStoreBargainUserQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户参与砍价表ID")
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


    private Double residuePrice;

    private String title;

    private String image;

    private Date datatime;

}
