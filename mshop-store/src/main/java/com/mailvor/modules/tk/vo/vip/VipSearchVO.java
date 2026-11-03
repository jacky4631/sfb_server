package com.mailvor.modules.tk.vo.vip;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "唯品会搜索商品vo")
@Data
public class VipSearchVO {

    @JsonProperty("goodsId")
    @Schema(description = "商品id")
    private String goodsId;

    @JsonProperty("goodsName")
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品短标题")
    private String dtitle;

    @JsonProperty("productSales")
    @Schema(description = "销量")
    private String sales;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "vip";


//    @JsonProperty("storeInfo.$storeName")
    @Schema(description = "店铺名称")
    private String shopName;

    @JsonProperty("goodsMainPicture")
    @Schema(description = "商品预览图")
    private String img;


    @JsonProperty("marketPrice")
    @Schema(description = "原价")
    private Double startPrice;


    @JsonProperty("vipPrice")
    @Schema(description = "现价")
    private Double endPrice;

    @JsonProperty("commission")
    @Schema(description = "佣金")
    private Double fee;
    @JsonProperty("commissionRate")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @Schema(description = "优惠券金额")
    private Double coupon;
    private VipGoodsDetailVO.StoreInfo storeInfo;
    private VipGoodsDetailCouponInfoVO couponInfo;

    public String getShopName() {
        return shopName != null ? shopName : storeInfo == null ? null : storeInfo.getStoreName();
    }
}
