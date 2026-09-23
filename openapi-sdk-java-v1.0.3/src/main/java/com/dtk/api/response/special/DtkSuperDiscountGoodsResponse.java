package com.dtk.api.response.special;

import com.dtk.api.response.putstorage.DtkBaseItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 折上折响应结果实体
 *
 * @author baige
 * @date 2020/11/26 14:28
 */
@Getter
@Setter
public class DtkSuperDiscountGoodsResponse extends DtkBaseItemResponse {
    private List<String> specialText;
    private String video;
    private BigDecimal quanMLink;
    private BigDecimal hzQuanOver;
    private Integer yunfeixian;
    private BigDecimal estimateAmount;
    private Integer freeshipRemoteDistrict;
    private List<Integer> marketGroup;
}
