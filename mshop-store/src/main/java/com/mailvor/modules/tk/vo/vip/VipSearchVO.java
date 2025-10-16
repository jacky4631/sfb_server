package com.mailvor.modules.tk.vo.vip;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "唯品会搜索商品vo")
@Data
public class VipSearchVO {

    @JSONField(name = "goodsId")
    @Schema(description = "商品id")
    private String goodsId;

    @JSONField(name = "goodsName")
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "商品短标题")
    private String dtitle;

    @JSONField(name = "productSales")
    @Schema(description = "销量")
    private String sales;

    @Schema(description = "平台 tb tm jd pdd dy vip mt")
    private String platform = "vip";


//    @JSONField(name = "storeInfo.$storeName")
    @Schema(description = "店铺名称")
    private String shopName;

    @JSONField(name = "goodsMainPicture")
    @Schema(description = "商品预览图")
    private String img;


    @JSONField(name = "marketPrice")
    @Schema(description = "原价")
    private Double startPrice;


    @JSONField(name = "vipPrice")
    @Schema(description = "现价")
    private Double endPrice;

    @JSONField(name = "commission")
    @Schema(description = "佣金")
    private Double fee;
    @JSONField(name = "commissionRate")
    @Schema(description = "佣金比例")
    private Double feeRatio;

    @Schema(description = "优惠券金额")
    private Double coupon;
}
