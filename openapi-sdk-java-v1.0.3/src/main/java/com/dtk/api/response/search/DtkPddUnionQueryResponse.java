package com.dtk.api.response.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 14:22
 */
@Getter
@Setter
public class DtkPddUnionQueryResponse {

    private List<GoodsListInfo> goodsList;
    private String listId;
    private String searchId;
    private String totalCount;

    @Data
    public static class GoodsListInfo {
        private String couponDiscount;
        private String couponEndTime;
        private String couponMinOrderAmount;
        private String couponRemainQuantity;
        private String couponStartTime;
        private String couponTotalQuantity;
        private String descTxt;
        private String goodsDesc;
        private String goodsImageUrl;
        private String goodsName;
        private String goodsSign;
        private String goodsThumbnailUrl;
        private String hasCoupon;
        private String hasMallCoupon;
        private String lgstTxt;
        private String mallCouponDiscountPct;
        private String mallCouponEndTime;
        private String mallCouponId;
        private String mallCouponMaxDiscountAmount;
        private String mallCouponMinOrderAmount;
        private String mallCouponRemainQuantity;
        private String mallCouponStartTime;
        private String mallCouponTotalQuantity;
        private String mallCps;
        private String mallId;
        private String mallName;
        private String merchantType;
        private String minGroupPrice;
        private String minNormalPrice;
        private String onlySceneAuth;
        private String optId;
        private String optName;
        private String planType;
        private String predictPromotionRate;
        private String promotionRate;
        private String salesTip;
        private String searchId;
        private String servTxt;
        private String shareRate;
        private String zsDuoId;
        private List<String> activityTags;
        private List<String> catIds;
        private List<String> optIds;
        private List<String> categories;
        private String brandName;
    }
}
