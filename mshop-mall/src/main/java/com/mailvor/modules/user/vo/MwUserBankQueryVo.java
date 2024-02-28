package com.mailvor.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "MwUserBankQueryVo对象", description = "用户银行卡表查询参数")
public class MwUserBankQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户银行卡id")
    private Long id;

//    @ApiModelProperty(value = "用户id")
//    private Long uid;

    @ApiModelProperty(value = "银行卡号")
    private String bankNo;
    @ApiModelProperty(value = "银行卡号")
    private String bankNoFull;

    @ApiModelProperty(value = "银行卡号")
    private String bankName;
//
//    @ApiModelProperty(value = "签约成功的协议号")
//    private String protocolNo;
    @ApiModelProperty(value = "银行卡类型 DEBIT=储蓄卡 CREDIT=信用卡")
    private String cardType;

    @ApiModelProperty(value = "支付默认")
    private Integer isDefault;
    @ApiModelProperty(value = "提现默认")
    private Integer extractDefault;
    @ApiModelProperty(value = "手机号")
    private String phone;

}
