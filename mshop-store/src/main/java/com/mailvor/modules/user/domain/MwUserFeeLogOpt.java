package com.mailvor.modules.user.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import lombok.*;
import lombok.experimental.Accessors;

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
@TableName(value = "mw_user_fee_log_opt",autoResultMap = true)
public class MwUserFeeLogOpt extends BaseDomain {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "平台,tb jd pdd dy vip fans up")
//    @ApiModelProperty(value = "type时间 1=今天 2=昨天 3=本月 4=上月 5=近30天 6近7天")
//    @ApiModelProperty(value = "cid佣金类型 1=总览 2=自购 4=金客 5=银客 6=已结算")
    @TableId
    private Long uid;

    //tt=今日 ts=近七日 tm=本月 tl=上月
//    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String tt;
//    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String ts;
//    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String tm;
//    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String tl;

}
