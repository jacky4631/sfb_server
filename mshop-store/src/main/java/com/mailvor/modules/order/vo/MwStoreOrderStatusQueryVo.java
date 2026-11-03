package com.mailvor.modules.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 订单操作记录表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-27
 */
@Data
@Schema(description = "订单操作记录表查询参数")
public class MwStoreOrderStatusQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Schema(description = "订单id")
    private Integer oid;

    @Schema(description = "操作类型")
    private String changeType;

    @Schema(description = "操作备注")
    private String changeMessage;

    @Schema(description = "操作时间")
    private Integer changeTime;

}
