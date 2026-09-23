package com.dtk.api.response.special;

import lombok.Data;

import java.util.List;

/**
 * 每日半价响应结果实体
 *
 * @author baige
 * @date 2020/11/12 10:02
 */
@Data
public class DtkGetHalfPriceDayResponse {
    private DtkGetHalfPriceDayPriceResponse halfPriceInfo;
    private List<DtkGetHalfPriceDaySessionResponse> sessionsList;
}
