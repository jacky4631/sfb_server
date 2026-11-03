package com.mailvor.modules.user.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.user.service.dto.TaskDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 设置用户等级表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-06
 */
@Data
@Schema(description = "设置用户等级表查询参数")
public class MwSystemUserLevelQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "会员名称")
    private String name;

    @Schema(description = "购买金额")
    private BigDecimal money;

    @Schema(description = "有效时间")
    private Integer validDate;

    @Schema(description = "会员等级")
    private Integer grade;

    @Schema(description = "分佣比例")
    private BigDecimal discount;

    private BigDecimal discountOne;

    private BigDecimal discountTwo;

    private Integer isForever;

    private String rechargeId;

    /**
     * platform
     * */
    private String type;

    private String iosProductId;


}
