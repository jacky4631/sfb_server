package com.dtk.api.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 16:52
 */
@Getter
@Setter
public class DtkSingleAlbumCommodityResponse {
    private String albumId;
    private String albumName;
    private String userName;
    private String headImg;
    private List<String> userGoodAt;
    private String goodsCount;
    private String releaseTime;
    private String endTime;
    private List<GoodsListInfo2> goodsList;

    @Data
    public static class GoodsListInfo2 {
        private String id;
        private String goodsId;
        private String title;
        private String mainPic;
        private List<String> specialText;
        private String originPrice;
        private String actualPrice;
        private String activityType;
        private String shopType;
        private String monthSales;
        private String dailySales;
        private String couponId;
        private String couponLink;
        private String couponPrice;
        private String couponReceiveNum;
        private String couponTotalNum;
        private String commissionRate;
        private String discount;
    }
}
