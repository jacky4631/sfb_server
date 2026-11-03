package com.mailvor.modules.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@Schema(description = "用户等级记录表")
public class MwUserLevel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户uid")
    private Long uid;

    @Schema(description = "等级vip")
    private Integer levelId;

    @Schema(description = "会员等级")
    private Integer grade;

    @Schema(description = "过期时间")
    private Integer validTime;

    @Schema(description = "是否永久")
    private Integer isForever;

    @Schema(description = "商户id")
    private Integer merId;

    @Schema(description = "0:禁止,1:正常")
    private Integer status;

    @Schema(description = "备注")
    private String mark;

    @Schema(description = "是否已通知")
    private Integer remind;


    @Schema(description = "分佣比例")
    private Integer discount;

    @Schema(description = "平台 tb jd pdd dy vip")
    private String platform;

}
