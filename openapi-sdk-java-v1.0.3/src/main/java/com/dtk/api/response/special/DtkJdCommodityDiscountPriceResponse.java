package com.dtk.api.response.special;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 14:49
 */
@Getter
@Setter
public class DtkJdCommodityDiscountPriceResponse {
    private String skuId;
    private String skuName;
    private String materialUrl;
    private String commissionRate;
    private String couponUrl;
    private List<PriceListInfo> priceList;

    @Data
    public static class PriceListInfo {
        private String sdate;
        private String sprice;
    }
}
