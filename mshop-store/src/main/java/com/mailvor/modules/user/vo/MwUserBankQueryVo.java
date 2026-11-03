package com.mailvor.modules.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户地址表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-28
 */
@Data
@Schema(description = "用户银行卡表查询参数")
public class MwUserBankQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户银行卡id")
    private Long id;

//    @Schema(description = "用户id")
//    private Long uid;

    @Schema(description = "银行卡号")
    private String bankNo;
    @Schema(description = "银行卡号")
    private String bankNoFull;

    @Schema(description = "银行卡号")
    private String bankName;
//
//    @Schema(description = "签约成功的协议号")
//    private String protocolNo;
    @Schema(description = "银行卡类型 DEBIT=储蓄卡 CREDIT=信用卡")
    private String cardType;

    @Schema(description = "支付默认")
    private Integer isDefault;
    @Schema(description = "提现默认")
    private Integer extractDefault;
    @Schema(description = "手机号")
    private String phone;

}
