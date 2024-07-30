package com.mailvor.modules.utils;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class PayUtil {

    //支付宝支付 1原生 2三方
    public static final List<Integer> CHANNEL_TYPE_IOS_PAY = Arrays.asList(0);
    //支付宝支付 1原生 2三方
    public static final List<Integer> CHANNEL_TYPE_ALI_PAY = Arrays.asList(1,2);
    //微信支付6原生 7三方
    public static final List<Integer> CHANNEL_TYPE_WECHAT = Arrays.asList(6,7);

    //银行卡支付 12
    public static final List<Integer> CHANNEL_TYPE_BANK = Arrays.asList(12);
    //银行卡绑卡支付 13
    public static final List<Integer> CHANNEL_TYPE_BANK_BIND = Arrays.asList(13);

    public static final List<Integer> CHANNEL_TYPE_YUNSHANFU = Arrays.asList(15);

}
