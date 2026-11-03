package com.mailvor.modules.tk.vo.pdd;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多搜索商品vo")
@Data
public class PddSearchVO {

    @JsonProperty("goodsSign")
    @Schema(description = "商品id")
    private String goodsId;

    @JsonProperty("goodsDesc")
    @Schema(description = "商品标题")
    private String title;
    @JsonProperty("goodsName")
    @Schema(description = "商品短标题")
    private String dtitle;

    @JsonProperty("salesTip")
    @Schema(description = "销量")
    private String sales;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "pdd";


    @JsonProperty("mallName")
    @Schema(description = "店铺名称")
    private String shopName;

    @JsonProperty("goodsImageUrl")
    @Schema(description = "商品预览图")
    private String img;


    @JsonProperty("minNormalPrice")
    @Schema(description = "原价")
    private Double startPrice;


    @JsonProperty("minGroupPrice")
    @Schema(description = "现价")
    private Double endPrice;

    @Schema(description = "佣金")
    private Double fee;
    @JsonProperty("promotionRate")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @JsonProperty("couponDiscount")
    @Schema(description = "优惠券金额")
    private Double coupon;
}
