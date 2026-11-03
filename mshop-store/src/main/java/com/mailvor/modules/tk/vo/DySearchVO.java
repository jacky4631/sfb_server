package com.mailvor.modules.tk.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多搜索商品vo")
@Data
public class DySearchVO {

    @JsonProperty("product_id")
    @Schema(description = "商品id")
    private String goodsId;

    @JsonProperty("product_title")
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品短标题")
    private String dtitle;

    @JsonProperty("sales")
    @Schema(description = "销量")
    private String sales;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "dy";

    @JsonProperty("shop_name")
    @Schema(description = "店铺名称")
    private String shopName;

    @JsonProperty("item_pic")
    @Schema(description = "商品预览图")
    private String img;


    @JsonProperty("price")
    @Schema(description = "原价")
    private Double startPrice;


    @JsonProperty("end_price")
    @Schema(description = "现价")
    private Double endPrice;

    @JsonProperty("dymoney")
    @Schema(description = "佣金")
    private Double fee;
    @JsonProperty("dyrates")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @JsonProperty("coupon_price")
    @Schema(description = "优惠券金额")
    private Double coupon;


    @JsonProperty("cate_name")
    @Schema(description = "分类名称")
    private String cateName;

    @JsonProperty("detail_url")
    @Schema(description = "商品链接")
    private String itemUrl;
}
