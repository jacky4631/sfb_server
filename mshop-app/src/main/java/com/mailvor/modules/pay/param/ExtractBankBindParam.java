package com.mailvor.modules.pay.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class ExtractBankBindParam implements Serializable {

    @ApiModelProperty(value = "银行id,bankId和bankNo phone只需要传一种，bankId存在时优先取bankId")
    private Long bankId;
    @ApiModelProperty(value = "银行卡号")
    private String bankNo;

    @ApiModelProperty(value = "手机号")
    private String phone;

}
