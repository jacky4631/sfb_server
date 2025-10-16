package com.mailvor.modules.tk.vo.pdd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多搜索商品vo")
@Data
public class PddSearchVO {

    @JSONField(name = "goodsSign")
    @Schema(description = "商品id")
    private String goodsId;

    @JSONField(name = "goodsDesc")
    @Schema(description = "商品标题")
    private String title;
    @JSONField(name = "goodsName")
    @Schema(description = "商品短标题")
    private String dtitle;

    @JSONField(name = "salesTip")
    @Schema(description = "销量")
    private String sales;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "pdd";


    @JSONField(name = "mallName")
    @Schema(description = "店铺名称")
    private String shopName;

    @JSONField(name = "goodsImageUrl")
    @Schema(description = "商品预览图")
    private String img;


    @JSONField(name = "minNormalPrice")
    @Schema(description = "原价")
    private Double startPrice;


    @JSONField(name = "minGroupPrice")
    @Schema(description = "现价")
    private Double endPrice;

    @Schema(description = "佣金")
    private Double fee;
    @JSONField(name = "promotionRate")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @JSONField(name = "couponDiscount")
    @Schema(description = "优惠券金额")
    private Double coupon;
}
