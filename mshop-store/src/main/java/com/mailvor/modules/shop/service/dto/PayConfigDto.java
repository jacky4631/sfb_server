package com.mailvor.modules.shop.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName RechargeParam
 * @author huangyu
 * @Date 2024/3/4
 **/
@Data
public class PayConfigDto implements Serializable {
    @NotBlank(message = "支付宝支付开关不能为空")
    @Schema(description = "支付宝支付开关 1=开 0=关 下同")
    private String alipay = "1";
    @NotBlank(message = "微信支付开关不能为空")
    @Schema(description = "微信支付开关")
    private String wechat = "0";
    @NotBlank(message = "银行卡支付开关不能为空")
    @Schema(description = "银行卡支付开关")
    private String bank = "0";

    @NotBlank(message = "银行卡绑卡支付开关不能为空")
    @Schema(description = "银行卡绑卡支付开关")
    private String bankBind = "0";

    @NotBlank(message = "支付宝支付减免开关不能为空")
    @Schema(description = "支付宝支付减免开关")
    private String ac = "0";

    @NotBlank(message = "微信支付减免开关不能为空")
    @Schema(description = "微信支付减免开关")
    private String wc = "0";

    @NotBlank(message = "银行卡支付减免开关不能为空")
    @Schema(description = "银行卡支付减免开关")
    private String bc = "0";

    @NotBlank(message = "银行卡绑卡支付减免开关不能为空")
    @Schema(description = "银行卡绑卡支付减免开关")
    private String bbc = "0";
}
