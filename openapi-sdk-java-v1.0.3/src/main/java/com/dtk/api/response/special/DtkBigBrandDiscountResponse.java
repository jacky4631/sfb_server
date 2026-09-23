package com.dtk.api.response.special;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 15:02
 */
@Getter
@Setter
public class DtkBigBrandDiscountResponse {
    private List<ListInfo> list;
    private String pageId;
    private String totalNum;
    private String goScroll;

    @Data
    public static class ListInfo {
        private String cid1;
        private String cid1Name;
        private String cid2;
        private String cid3;
        private String cid2Name;
        private String cid3Name;
        private String skuId;
        private String skuName;
        private String originPrice;
        private String actualPrice;
        private String picMain;
        private String materialUr;
        private String isFreeFreightRisk;
        private String isFreeShipping;
        private String isFlagship;
        private String isSeckill;
        private String inOrderCount30Days;
        private String isOwner;
        private String comments;
        private String goodsCommentShare;
        private String shopId;
        private String shopName;
        private String commission;
        private String commissionShare;
        private String commissionStartTime;
        private String commissionEndTime;
        private String couponType;
        private String couponLink;
        private String couponAmount;
        private String couponConditions;
        private String couponStartTime;
        private String couponEndTime;
        private String couponUseStartTime;
        private String couponUserEndTime;
        private String couponTotalCount;
        private String couponRemainCount;
        private String couponReceiveCount;
        private String extensionContent;
        private String productIntro;
        private List<String> smallImages;
        private List<String> detailImages;
        private List<String> videoUrlList;
        private List<PromotionLabelListVO> promotionLabelList;
    }

    @Data
    public static class PromotionLabelListVO {
        private String labelName;
        private String promotionLabel;
        private String promotionLabelId;
        private String startTime;
        private String endTime;
    }
}
