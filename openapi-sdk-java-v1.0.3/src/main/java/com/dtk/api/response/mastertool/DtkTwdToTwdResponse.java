package com.dtk.api.response.mastertool;

import lombok.Getter;
import lombok.Setter;

/**
 * 淘口令转淘口令响应结果实体
 *
 * @author 1
 * @date 2020/11/11 16:14
 */
@Getter
@Setter
public class DtkTwdToTwdResponse {
    private String itemId;
    private String tpwd;
    private String longTpwd;
    private String maxCommissionRate;
    private String minCommissionRate;
    private String originUrl;
    private String title;
    private String couponClickUrl;
    private String couponInfo;
    private String couponEndTime;
    private String couponStartTime;
    private String couponTotalCount;
    private String couponRemainCount;
    private String shortUrl;
}
