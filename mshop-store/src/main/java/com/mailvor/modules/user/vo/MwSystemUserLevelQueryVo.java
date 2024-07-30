package com.mailvor.modules.user.vo;


import com.mailvor.modules.user.service.dto.TaskDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "MwSystemUserLevelQueryVo对象", description = "设置用户等级表查询参数")
public class MwSystemUserLevelQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "会员名称")
    private String name;

    @ApiModelProperty(value = "购买金额")
    private BigDecimal money;

    @ApiModelProperty(value = "有效时间")
    private Integer validDate;

    @ApiModelProperty(value = "月卡购买金额")
    private BigDecimal monthMoney;

    @ApiModelProperty(value = "月卡有效时间")
    private Integer monthValidDate;

    @ApiModelProperty(value = "会员等级")
    private Integer grade;

    @ApiModelProperty(value = "分佣比例")
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
