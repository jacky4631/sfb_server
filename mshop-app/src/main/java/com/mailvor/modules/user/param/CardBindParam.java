package com.mailvor.modules.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2019/12/8
 **/
@Data
public class CardBindParam implements Serializable {
    @NotBlank(message = "银行卡号不能为空")
    @Schema(description = "银行卡号")
    private String no;
}
