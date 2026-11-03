package com.mailvor.modules.tk.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailvor.enums.PlatformEnum;
import com.mailvor.common.json.JsonObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "商品通用vo")
@Data
public class DyGoodsDetailVO {
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

    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "店铺logo")
    private String shopLogo;

    @JsonProperty("douyin_images")
    @Schema(description = "商品主图")
    private List<String> banners;

    @Schema(description = "商品详情图")
    private List<String> details;

    @JsonProperty("coupon_price")
    @Schema(description = "优惠券金额")
    private Double coupon;
    @Schema(description = "优惠券使用限制")
    private Double couponLimit;

    @Schema(description = "优惠券开始时间")
    private String couponStart;
    @Schema(description = "优惠券结束时间")
    private String couponEnd;

    @JsonProperty("category_name")
    @Schema(description = "分类")
    private String cateName;

    @JsonProperty("shop_total_score")
    @Schema(description = "好评率")
    private JsonObject shopTotalScore;

    @JsonProperty("detail_url")
    @Schema(description = "商品链接")
    private String itemUrl;
    @Schema(description = "描述分")
    private String descScore;
    @Schema(description = "店铺分")
    private String shipScore;
    @Schema(description = "服务分")
    private String serviceScore;


    @JsonProperty("logistics_info")
    @Schema(description = "物流发货时间")
    private String logisticsInfo;

    @Schema(description = "平台")
    private String platform = PlatformEnum.DY.getValue();

}
