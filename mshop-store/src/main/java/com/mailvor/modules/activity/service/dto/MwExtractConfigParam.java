/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MwExtractConfigParam{

    /**
     * 提现最低金额
     * */
    @NotNull(message = "最低提现金额不能为空")
    @Min(value = 0, message = "最低提现金额必须大于等于0")
    private Integer minPrice = 1;

    /**
     * 提现最高金额
     * */
    @NotNull(message = "最高提现金额不能为空")
    @Min(value = 0, message = "最高提现金额必须大于等于0")
    private Integer maxPrice = 3000;

    /**
     * 每天提现次数
     * */
    @NotNull(message = "每天提现次数不能为空")
    @Min(value = 1, message = "每天提现次数必须大于0")
    private Integer count = 1;

    /**
     * 自动提现 1开启 2关闭
     * */
    @NotNull(message = "自动提现不能为空")
    @Range(min = 1, max = 2, message = "自动提现必须是1=开启 2=关闭")
    private Integer auto = 1;


    /**
     * 自动提现最大金额设置
     * */
    @NotNull(message = "自动提现最大金额不能为空")
    @Min(value = 0, message = "自动提现最大金额必须大于等于0")
    private Integer autoMax = 3000;

    /**
     * app端提现文字设置：每天可提现一次
     * */
    @NotBlank(message = "app端提现文字设置不能为空")
    private String extractIntervalDesc = "每天可提现一次";
    /**
     * app端提现文字设置：满1元可提现
     * */
    @NotBlank(message = "满1元可提现")
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
    @Range(min = 0, max = 1, message = "是否开启微信提现必须是1=开启 0=关闭")
    private Integer weixin = 0;
    /**
     * 是否开启支付宝提现 1开启 0关闭
     * */
    @NotNull(message = "是否开启支付宝提现不能为空")
    @Range(min = 0, max = 1, message = "是否开启支付宝提现必须是1=开启 0=关闭")
    private Integer alipay = 1;
    /**
     * 是否开启银行卡提现 1开启 0关闭，需要对接易宝支付，银盛支付
     * */
    @NotNull(message = "是否开启银行卡提现不能为空")
    @Range(min = 0, max = 1, message = "是否开启银行卡提现必须是1=开启 0=关闭")
    private Integer bank = 0;
    /**
     * 提现手续费，一般设置5%
     * */
    @NotNull(message = "最低提现金额不能为空")
    @Min(value = 0, message = "提现手续费必须大于等于0")
    private Integer charge = 0;
}
