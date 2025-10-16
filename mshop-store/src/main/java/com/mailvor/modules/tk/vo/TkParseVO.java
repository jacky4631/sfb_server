package com.mailvor.modules.tk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一数据结构
 * */
@Schema(description = "淘宝榜单vo")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TkParseVO {

    @JSONField(name = "item_id")
    @Schema(description = "商品id")
    private String goodsId;
    @JSONField(name = "sku_id")
    @Schema(description = "京东商品id")
    private String skuId;
    @JSONField(name = "item_title")
    @Schema(description = "商品标题")
    private String title;

    @JSONField(name = "item_price")
    @Schema(description = "原价")
    private Double startPrice;

    @JSONField(name = "item_end_price")
    @Schema(description = "现价")
    private Double endPrice;
    @JSONField(name = "plat_type")
    @Schema(description = "平台 描述：1.淘宝  2.京东  3.拼多多  4.抖音 5=唯品会")
    private Integer platType;
    @JSONField(name = "item_pic")
    @Schema(description = "商品预览图")
    private String img;
    @JSONField(name = "rates")
    @Schema(description = "佣金比例")
    private Double feeRatio;
    @JSONField(name = "item_url")
    @Schema(description = "商品链接")
    private String itemUrl;

    @JSONField(name = "month_sale")
    @Schema(description = "销量")
    private String sales;
    @JSONField(name = "coupon_money")
    @Schema(description = "优惠券金额")
    private Double coupon;

    @Schema(description = "原内容")
    private String originContent;


    @Schema(description = "转链后的url，目前只有淘宝有")
    private String shortUrl;

    @Schema(description = "转链后的口令，目前只有淘宝有")
    private String shortPwd;


}
