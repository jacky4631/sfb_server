/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.RankCoupon;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.RankGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.SkuTagInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一数据结构
 * */
@Schema(description = "京东搜索商品vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdUnionSearchVO {

    @JsonProperty("itemId")
    @Schema(description = "商品id")
    private String goodsId;

    @JsonProperty("skuName")
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品短标题")
    private String dtitle;

    @JsonProperty("inOrderCount30Days")
    @Schema(description = "销量")
    private String sales;
    @Schema(description = "评论数")
    private String comments;
    @Builder.Default
    @Schema(description = "平台 tb jd pdd dy vip mt")
    private String platform = "jd";


    @Schema(description = "店铺名称")
    private String shopName;

    @JsonProperty("imageUrl")
    @Schema(description = "商品预览图")
    private String img;
    @JsonProperty("whiteImage")
    @Schema(description = "商品预览图")
    private String whiteImg;

    @JsonProperty("lowestPrice")
    @Schema(description = "原价")
    private Double startPrice;


    @JsonProperty("lowestCouponPrice")
    @Schema(description = "现价")
    private Double endPrice;

    @JsonProperty("couponCommission")
    @Schema(description = "佣金")
    private Double fee;
    @JsonProperty("commissionShare")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @Schema(description = "优惠券金额")
    private Double coupon;

    @Schema(description = "商品类型：自营[g]，POP[p]")
    private String owner;


    public static JdUnionSearchVO convert(RankGoodsResp resp) {
        JdUnionSearchVO searchVO = JdUnionSearchVO.builder()
                .goodsId(resp.getItemId())
                .title(resp.getSkuName())
                .comments(resp.getComments().toString())
//                .shopName(resp.getSkuTagList())
                .img(resp.getImageUrl())
                .startPrice(resp.getWlprice())
                .endPrice(resp.getPurchasePriceInfo().getPurchasePrice())
                .fee(BigDecimal.valueOf(resp.getCommissionShare() * resp.getPurchasePriceInfo().getPurchasePrice()/100).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .feeRatio(resp.getCommissionShare())
                .build();
        RankCoupon[] coupons = resp.getPurchasePriceInfo().getCouponList();
        if(coupons != null && coupons.length > 0) {
            RankCoupon coupon1 = coupons[0];
            searchVO.setCoupon(coupon1.getDiscount());
        }
        SkuTagInfo[] skuTagInfos = resp.getSkuTagList();
        if(skuTagInfos != null) {
            if(skuTagInfos.length > 1) {
                searchVO.setShopName(resp.getSkuTagList()[0].getName() + " | " + resp.getSkuTagList()[1].getName());
            } else if(skuTagInfos.length > 0) {
                searchVO.setShopName(resp.getSkuTagList()[0].getName());
            }

        }
        return searchVO;
    }

    public static List<JdUnionSearchVO> convert(RankGoodsResp[] resp) {
        return Arrays.stream(resp).map(rankGoodsResp -> convert(rankGoodsResp)).collect(Collectors.toList());
    }
}
