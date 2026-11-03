package com.mailvor.modules.user.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import lombok.*;

/**
 * <p>
 * 用户地址表
 * </p>
 *
 * @author huangyu
 * @since 2019-10-28
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户银行卡表")
@TableName("mw_user_bank")
public class MwUserBank extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户银行卡id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "银行卡号")
    private String bankNo;
    @Schema(description = "银行名称")
    private String bankName;
    @Schema(description = "银行行别编号")
    private String bankCode;
    @Schema(description = "银行卡类型 DEBIT-借记卡   CREDIT-贷记卡")
    private String cardType;
    @Schema(description = "签约成功的协议号")
    private String protocolNo;
    @Schema(description = "业务请求号")
    private String requestNo;
    @Schema(description = "支付默认 1=默认")
    private Integer isDefault;
    @Schema(description = "提现默认 1=默认")
    private Integer extractDefault;
    @Schema(description = "是否签约 0=未签约 1=签约")
    private Integer sign;

    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "提现绑卡标识")
    private String linkId;
}
