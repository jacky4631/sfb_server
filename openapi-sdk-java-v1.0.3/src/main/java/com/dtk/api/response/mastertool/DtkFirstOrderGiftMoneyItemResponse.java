package com.dtk.api.response.mastertool;

import com.dtk.api.response.putstorage.DtkBaseItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首单礼金商品响应结果信息项实体
 *
 * @author 1
 * @date 2020/11/11 16:21
 */
@Getter
@Setter
public class DtkFirstOrderGiftMoneyItemResponse extends DtkBaseItemResponse {
    private BigDecimal quanMLink;
    private BigDecimal hzQuanOver;
    private Integer yunfeixian;
    private BigDecimal estimateAmount;
    private List<String> specialText;
    private Integer freeshipRemoteDistrict;
    private String video;
    private String firstOrderAmount;
    private BigDecimal estimatePrice;
}
