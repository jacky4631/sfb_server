package com.mailvor.modules.user.rest.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class BankBindParam implements Serializable {
    @Schema(description = "银行卡号")
    private String bankNo;

    @Schema(description = "手机号")
    private String phone;

}
