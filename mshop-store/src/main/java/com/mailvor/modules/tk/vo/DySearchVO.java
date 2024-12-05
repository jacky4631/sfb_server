package com.mailvor.modules.tk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多搜索商品vo")
@Data
public class DySearchVO {

    @JSONField(name = "itemid")
    @Schema(description = "商品id")
    private String goodsId;

    @JSONField(name = "itemtitle")
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品短标题")
    private String dtitle;

    @JSONField(name = "sales")
    @Schema(description = "销量")
    private String sales;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "dy";

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

    @JSONField(name = "coupon_price")
    @Schema(description = "优惠券金额")
    private Double coupon;


    @JSONField(name = "cate_name")
    @Schema(description = "分类名称")
    private String cateName;
}
