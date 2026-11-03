package com.mailvor.modules.pay.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class ExtractBankBindParam implements Serializable {

    @Schema(description = "银行id,bankId和bankNo phone只需要传一种，bankId存在时优先取bankId")
    private Long bankId;
    @Schema(description = "银行卡号")
    private String bankNo;

    @Schema(description = "手机号")
    private String phone;

}
