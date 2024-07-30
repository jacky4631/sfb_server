package com.mailvor.modules.energy.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mailvor.domain.BaseDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户热度订单
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
public class UserEnergyOrderDto extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户热度订单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "UserEnergyLog id")
    private Long logId;
    @ApiModelProperty(value = "UserEnergyLog id")
    private Integer logType;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "拆红包金额")
    private BigDecimal releaseMoney;

    @ApiModelProperty(value = "发放时间")
    private Date releaseTime;
    @ApiModelProperty(value = "是否拆红包 0=未拆 1=已拆")
    private Integer hb;
    @ApiModelProperty(value = "是否已发放给用户 0=未发放 1=已发放")
    private Integer paid;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "平台 tb jd pdd dy vip")
    private String platform;

    @ApiModelProperty(value = "是否退款订单 0=默认 1=需要退款 2=已退款")
    private Integer refund;

    @ApiModelProperty(value = "解锁时间")
    private Date unlockTime;

}
