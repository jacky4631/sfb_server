package com.dtk.api.response.mastertool;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 17:21
 */
@Getter
@Setter
public class DtkJdOrderQueryResponse {
    private String id;
    private String orderId;
    private String parentId;
    private String orderTime;
    private String finishTime;
    private String modifyTime;
    private String orderEmt;
    private String plus;
    private String unionId;
    private String skuId;
    private String skuName;
    private String skuNum;
    private String skuReturnNum;
    private String skuFrozenNum;
    private String price;
    private String commissionRate;
    private String subSideRate;
    private String subsidyRate;
    private String finalRate;
    private String estimateCosPrice;
    private String estimateFee;
    private String actualCosPrice;
    private String actualFee;
    private String validCode;
    private String traceType;
    private String positionId;
    private String siteId;
    private String unionAlias;
    private String pid;
    private String cid1;
    private String cid2;
    private String cid3;
    private String subUnionId;
    private String unionTag;
    private String popId;
    private String ext1;
    private String payMonth;
    private String cpActId;
    private String unionRole;
    private String giftCouponOcsAmount;
    private String giftCouponKey;
    private String balanceExt;
    private String proPriceAmount;
    private String rid;
    private GoodsMessage goodsInfo;
    private CategoryMessage categoryInfo;

    @Data
    public static class GoodsMessage {
        private String imageUrl;
        private String owner;
        private String mainSkuId;
        private String productId;
        private String shopId;
    }

    @Data
    public static class CategoryMessage {
        private String cid1;
        private String cid2;
        private String cid3;
    }
}
