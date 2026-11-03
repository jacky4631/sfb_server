package com.mailvor.modules.tk.vo.vip;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailvor.enums.PlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "商品通用vo")
@Data
public class VipGoodsDetailVO {
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

    @Schema(description = "平台")
    private String platform = PlatformEnum.VIP.getValue();

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
    @Schema(description = "商品描述")
    private String desc;
    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "店铺名称")
    private String shopName;

    @JsonProperty("brandLogoFull")
    @Schema(description = "店铺logo")
    private String shopLogo;

    @JsonProperty("goodsCarouselPictures")
    @Schema(description = "商品主图")
    private List<String> banners;

    @JsonProperty("goodsDetailPictures")
    @Schema(description = "商品详情图")
    private List<String> details;

    @Schema(description = "优惠券使用限制")
    private Double couponLimit;

    @Schema(description = "优惠券开始时间")
    private String couponStart;
    @Schema(description = "优惠券结束时间")
    private String couponEnd;


    @JsonProperty("cat2ndName")
    @Schema(description = "优惠券结束时间")
    private String cateName;


    @Schema(description = "商品落地页")
    private String destUrl;
    @Schema(description = "用来标识获取推广物料的来源，请准确保存，并在调用转链接口时传入，防止错用")
    private String adCode;

    @Schema(description = "优惠券详情")
    private VipGoodsDetailCouponInfoVO couponInfo;

    // 定义storeInfo对象，用于接收嵌套字段
    @JsonProperty("storeInfo")
    private StoreInfo storeInfo;

    @Data
    // 内部类：存储店铺信息
    public static class StoreInfo {
        @JsonProperty("storeId")
        private String storeId;

        @JsonProperty("storeName")
        private String storeName;
    }


    // 提供一个便捷方法直接获取storeName
    public String getStoreName() {
        return storeInfo != null ? storeInfo.getStoreName() : null;
    }
}
