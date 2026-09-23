package com.dtk.api.response.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 联盟搜索响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:33
 */
@Getter
@Setter
public class DtkJdUnionQueryResponse {

    private List<JdCommodityInfo> list;
    private String totalNum;
    private String pageId;

    @Data
    public static class JdCommodityInfo {
        private String cid1;
        private String cid1Name;
        private String cid2;
        private String cid2Name;
        private String cid3;
        private String cid3Name;
        private String comments;
        private String commission;
        private String commissionShare;
        private String couponCommission;
        private String plusCommissionShare;
        private String isLock;
        private String commissionStartTime;
        private String commissionEndTime;
        private String goodCommentsShare;
        private String whiteImage;
        private String inOrderCount30Days;
        private String materialUrl;
        private String price;
        private String lowestPrice;
        private String lowestPriceType;
        private String lowestCouponPrice;
        private String shopName;
        private String shopId;
        private String shopLevel;
        private String shopLabel;
        private String userEvaluateScore;
        private String commentFactorScoreRankGrade;
        private String logisticsLvyueScore;
        private String logisticsFactorScoreRankGrade;
        private String afterServiceScore;
        private String afsFactorScoreRankGrade;
        private String scoreRankRate;
        private String skuId;
        private String skuName;
        private String spuid;
        private String brandCode;
        private String brandName;
        private String owner;
        private List<String> videoList;
        private List<String> commentList;
        private String document;
        private String discount;
        private List<CouponListInfo> couponList;
        private List<String> imageUrlList;
    }

    @Data
    public static class CouponListInfo {
        private String bindType;
        private String discount;
        private String link;
        private String platformType;
        private String quota;
        private String getStartTime;
        private String getEndTime;
        private String useStartTime;
        private String useEndTime;
        private String isBest;
        private String hotValue;
        private String isInputCoupon;
    }

}
