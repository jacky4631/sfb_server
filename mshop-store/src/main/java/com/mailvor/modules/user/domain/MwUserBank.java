package com.mailvor.modules.user.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mailvor.domain.BaseDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "MwUserBank对象", description = "用户银行卡表")
@TableName("mw_user_bank")
public class MwUserBank extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户银行卡id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "银行卡号")
    private String bankNo;
    @ApiModelProperty(value = "银行名称")
    private String bankName;
    @ApiModelProperty(value = "银行行别编号")
    private String bankCode;
    @ApiModelProperty(value = "银行卡类型 DEBIT-借记卡   CREDIT-贷记卡")
    private String cardType;
    @ApiModelProperty(value = "签约成功的协议号")
    private String protocolNo;
    @ApiModelProperty(value = "业务请求号")
    private String requestNo;
    @ApiModelProperty(value = "支付默认 1=默认")
    private Integer isDefault;
    @ApiModelProperty(value = "提现默认 1=默认")
    private Integer extractDefault;
    @ApiModelProperty(value = "是否签约 0=未签约 1=签约")
    private Integer sign;

    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "提现绑卡标识")
    private String linkId;
}
