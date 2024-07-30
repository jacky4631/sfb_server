package com.mailvor.modules.user.domain;

import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户等级记录表
 * </p>
 *
 * @author huangyu
 * @since 2019-12-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MwUserLevel对象", description = "用户等级记录表")
public class MwUserLevel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Long uid;

    @ApiModelProperty(value = "等级vip")
    private Integer levelId;

    @ApiModelProperty(value = "会员等级")
    private Integer grade;

    @ApiModelProperty(value = "过期时间")
    private Integer validTime;

    @ApiModelProperty(value = "是否永久")
    private Integer isForever;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "0:禁止,1:正常")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "是否已通知")
    private Integer remind;


    @ApiModelProperty(value = "分佣比例")
    private Integer discount;

    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;

}
