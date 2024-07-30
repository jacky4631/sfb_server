package com.mailvor.modules.activity.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MwExtractConfigDto {
    /**
     * app端提现文字设置：每天可提现一次
     * */
    @NotBlank(message = "app端提现文字设置不能为空")
    private String extractIntervalDesc = "每天可提现一次";
    /**
     * app端提现文字设置：满1元可提现
     * */
    @NotBlank(message = "app端提现文字设置不能为空")
    private String extractMinDesc = "满1元可提现";
    /**
     * app端提现文字设置："T+1天到账(5%手续费，T为工作日)"
     * */
    @NotBlank(message = "app端提现文字设置不能为空")
    private String extractFeeDesc = "T+1天到账(5%手续费，T为工作日)";
    /**
     * 是否开启微信提现 1开启 0关闭
     * */
    @NotNull(message = "是否开启微信提现不能为空")
    private String weixin = "0";
    /**
     * 是否开启支付宝提现 1开启 0关闭
     * */
    @NotNull(message = "是否开启支付宝提现不能为空")
    private String alipay = "1";
    /**
     * 是否开启银行卡提现 1开启 0关闭，需要对接易宝支付，银盛支付
     * */
    @NotNull(message = "是否开启银行卡提现不能为空")
    private String bank = "0";
    /**
     * 提现手续费，一般设置5%
     * */
    @NotNull(message = "最低提现金额不能为空")
    private String charge = "0";
    /**
     * 提现最低金额
     * */
    private String extractMin="1";

    /**
     * 提现最高金额
     * */
    private String extractMax="3000";

}
