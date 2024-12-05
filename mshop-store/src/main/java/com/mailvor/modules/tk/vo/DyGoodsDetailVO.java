package com.mailvor.modules.tk.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "商品通用vo")
@Data
public class DyGoodsDetailVO {
    @JSONField(name = "product_id")
    @Schema(description = "商品id")
    private String goodsId;

    @JSONField(name = "product_title")
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品短标题")
    private String dtitle;

    @JSONField(name = "sales")
    @Schema(description = "销量")
    private String sales;

    @JSONField(name = "shop_name")
    @Schema(description = "店铺名称")
    private String shopName;

    @JSONField(name = "item_pic")
    @Schema(description = "商品预览图")
    private String img;


    @JSONField(name = "price")
    @Schema(description = "原价")
    private Double startPrice;


    @JSONField(name = "end_price")
    @Schema(description = "现价")
    private Double endPrice;

    @JSONField(name = "dymoney")
    @Schema(description = "佣金")
    private Double fee;
    @JSONField(name = "dyrates")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "店铺logo")
    private String shopLogo;

    @JSONField(name = "douyin_images")
    @Schema(description = "商品主图")
    private List<String> banners;

    @Schema(description = "商品详情图")
    private List<String> details;

    @JSONField(name = "coupon_price")
    @Schema(description = "优惠券金额")
    private Double coupon;
    @Schema(description = "优惠券使用限制")
    private Double couponLimit;

    @Schema(description = "优惠券开始时间")
    private String couponStart;
    @Schema(description = "优惠券结束时间")
    private String couponEnd;

    @JSONField(name = "category_name")
    @Schema(description = "分类")
    private String cateName;

    @JSONField(name = "shop_total_score")
    @Schema(description = "好评率")
    private JSONObject shopTotalScore;

    @JSONField(name = "detail_url")
    @Schema(description = "商品链接")
    private String itemUrl;
    @Schema(description = "描述分")
    private String descScore;
    @Schema(description = "店铺分")
    private String shipScore;
    @Schema(description = "服务分")
    private String serviceScore;


    @JSONField(name = "logistics_info")
    @Schema(description = "物流发货时间")
    private String logisticsInfo;

}
