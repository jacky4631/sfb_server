package com.dtk.api.response.special;

import lombok.Data;

import java.util.List;

/**
 * 每日半价响应结果价格实体
 *
 * @author baige
 * @date 2020/11/12 10:07
 */
@Data
public class DtkGetHalfPriceDayPriceResponse {
    private String banner;
    private List<DtkGetHalfPriceDayPriceItemResponse> list;
}
