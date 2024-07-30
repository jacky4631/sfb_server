package com.mailvor.modules.pay.yeepay.dto;


import lombok.Data;

@Data
public class CardBinDto {
    /**
     * 1=成功 -1=失败
     * */
    private Integer code = -1;

    private String message = "该银行卡不支持";
    private String bankCardType;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行编码
     */
    private String bankCode;
}
