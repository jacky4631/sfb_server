package com.mailvor.modules.tk.vo.jd;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "京东商品vo")
@Data
public class JdGoodsVO {

    @JsonProperty("skuId")
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

    @Schema(description = "平台 tb jd pdd dy vip mt")
    private String platform = "jd";


    @Schema(description = "店铺名称")
    private String shopName;

    @JsonProperty("picMain")
    @Schema(description = "商品预览图")
    private String img;
    @JsonProperty("whiteImage")
    @Schema(description = "商品预览图")
    private String whiteImg;

    @JsonProperty("originPrice")
    @Schema(description = "原价")
    private Double startPrice;


    @JsonProperty("actualPrice")
    @Schema(description = "现价")
    private Double endPrice;

    @JsonProperty("commission")
    @Schema(description = "佣金")
    private Double fee;
    @JsonProperty("commissionShare")
    @Schema(description = "佣金比例")
    private Double feeRatio;
    @JsonProperty("couponAmount")
    @Schema(description = "优惠券金额")
    private Double coupon;

    @JsonProperty("isOwner")
    @Schema(description = "商品类型：自营[g]，POP[p]")
    private String owner;

}
