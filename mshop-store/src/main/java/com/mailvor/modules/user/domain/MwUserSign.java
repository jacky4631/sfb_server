package com.mailvor.modules.user.domain;


import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 签到记录表
 * </p>
 *
 * @author huangyu
 * @since 2019-12-05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MwUserSign对象", description = "签到记录表")
public class MwUserSign extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户uid")
    private Long uid;

    @ApiModelProperty(value = "签到说明")
    private String title;

    @ApiModelProperty(value = "获得积分")
    private Integer number;

    @ApiModelProperty(value = "剩余积分")
    private Integer balance;

}
