package com.mailvor.modules.user.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 用户收益统计
 * </p>
 *
 * @author huangyu
 * @since 2023-2-4
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@ToString
@TableName(value = "mw_user_fee_log",autoResultMap = true)
public class MwUserFeeLog extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户收益统计id,uid+type+platform+{cid}  cid==1时为空")
    private String id;

    @ApiModelProperty(value = "用户id")
    private Long uid;
    @ApiModelProperty(value = "佣金类型 1=总览 2=自购 3=体验订单 4=金客 5=银客 6=已结算 7=热度订单")
    private Integer cid;
    @ApiModelProperty(value = "时间 1=今天 2=昨天 3=本月 4=上月 5=近30天 6近7天")
    private Integer type;
    @ApiModelProperty(value = "平台,tb jd pdd dy vip fans up")
    private String platform;

    @ApiModelProperty(value = "渠道, 淘宝 。。。体验订单 用户")
    private String channel;

    @ApiModelProperty(value = "付款笔数")
    private Long count;


    @ApiModelProperty(value = "成交预估")
    private String fee;

    @ApiModelProperty(value = "原佣金")
    private BigDecimal feeValue;

}
