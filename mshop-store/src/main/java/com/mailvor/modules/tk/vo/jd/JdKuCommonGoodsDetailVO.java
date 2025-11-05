/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.alibaba.fastjson.annotation.JSONField;
import com.mailvor.enums.PlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Schema(description = "商品通用vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdKuCommonGoodsDetailVO {
    @Schema(description = "商品id")
    private String goodsId;

    @Schema(description = "京东商品id 用于转链")
    private String itemId;

    @Schema(description = "优惠券链接")
    private String couponLink;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品短标题")
    private String dtitle;

    @Schema(description = "销量")
    private String sales;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "商品预览图")
    private String img;

    @Schema(description = "原价")
    private Double startPrice;

    @Schema(description = "现价")
    private Double endPrice;

    @Schema(description = "佣金")
    private Double fee;

    @Schema(description = "佣金比例")
    private Double feeRatio;

    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "店铺logo")
    private String shopLogo;

    @Schema(description = "商品主图")
    private List<String> banners;

    @JSONField(name = "detailImages")
    @Schema(description = "商品详情图")
    private List<String> details;

    @Schema(description = "优惠券金额")
    private Double coupon;

    @Schema(description = "优惠券使用限制")
    private Double couponLimit;

    @Schema(description = "优惠券开始时间")
    private String couponStart;

    @Schema(description = "优惠券结束时间")
    private String couponEnd;

    @Schema(description = "分类")
    private String cateName;

    @Schema(description = "评论数")
    private String comments;

    @Schema(description = "好评率")
    private String goodsCommentShare;

    @Schema(description = "商品链接")
    private String itemUrl;

    @Schema(description = "京东自营 1=是 0=否")
    private String owner;

    @Schema(description = "描述分")
    private String descScore;

    @Schema(description = "店铺分")
    private String shipScore;

    @Schema(description = "服务分")
    private String serviceScore;

    @Schema(description = "平台")
    private String platform = PlatformEnum.JD.getValue();

    public static JdKuCommonGoodsDetailVO convert(JdKuGoodsDetailVO detailVO) {
        return JdKuCommonGoodsDetailVO.builder()
                .goodsId(detailVO.getGoodsId())
                .itemId(detailVO.getGoodsId())
                .couponLink(detailVO.getCouponLink())
                .title(detailVO.getTitle())
                .sales(detailVO.getSales())
                .comments(detailVO.getComments())
                .shopName(detailVO.getShopName())
                .img(detailVO.getImg())
                .startPrice(detailVO.getStartPrice())
                .endPrice(detailVO.getEndPrice())
                .fee(BigDecimal.valueOf(detailVO.getFeeRatio() * detailVO.getEndPrice()/100).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .feeRatio(detailVO.getFeeRatio())
                .coupon(detailVO.getCoupon())
                .couponStart(detailVO.getCouponStart())
                .couponEnd(detailVO.getCouponEnd())
                .cateName(detailVO.getCateInfo().getCateName())
                .goodsCommentShare(detailVO.getGoodsCommentShare())
                .owner(detailVO.getOwner())
                .descScore(detailVO.getShopInfo().getCommentFactorScoreRankGrade())
                .shipScore(detailVO.getShopInfo().getLogisticsFactorScoreRankGrade())
                .serviceScore(detailVO.getShopInfo().getAfsFactorScoreRankGrade())
                .details(Arrays.asList(detailVO.getDetails().split(",")))
                .platform(PlatformEnum.JD.getValue())
                .build();
    }
}
