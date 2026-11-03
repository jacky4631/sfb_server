package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author shenji
 * @date 2019-10-19
 */
@Data
@Schema(description = "商品表查询参数")
public class GoodsDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品id")
    private Long id;

    @Schema(description = "淘宝商品id")
    private String goodsId;

    @Schema(description = "商品淘宝链接")
    private String itemLink;

    @Schema(description = "淘宝标题")
    private String title;

    @Schema(description = "短标题")
    private String dtitle;

    @Schema(description = "特色文案（商品卖点）")
    private String specialText;

    @Schema(description = "券后价")
    private Double actualPrice;

    @Schema(description = "优惠券金额")
    private Double couponPrice;

    @Schema(description = "月销量")
    private Long monthSales;

    @Schema(description = "商品图")
    private String mainPic;

    @Schema(description = "商品描述")
    private String desc;

    @Schema(description = "商品原价")
    private Double originalPrice;

    @Schema(description = "历史低价，0.不是，1.史低")
    private Integer lowest;

    @Schema(description = "营销主图链接")
    private String marketingMainPic;

    @Schema(description = "商品视频")
    private String video;
    @Schema(description = "佣金比例")
    private Double commissionRate;
    @Schema(description = "优惠券链接")
    private String couponLink;
    @Schema(description = "券总量")
    private Long couponTotalNum;
    @Schema(description = "领券量")
    private Long couponReceiveNum;
    @Schema(description = "优惠券结束时间")
    private String couponEndTime;
    @Schema(description = "优惠券开始时间")
    private String couponStartTime;
    @Schema(description = "优惠券使用条件")
    private String couponConditions;

    @Schema(description = "2小时销量")
    private Long twoHoursSales;
    @Schema(description = "当天销量")
    private Long dailySales;
    @Schema(description = "是否是品牌商品")
    private Long brand;
    @Schema(description = "品牌id")
    private Long brandId;
    @Schema(description = "品牌名称")
    private String brandName;
    @Schema(description = "店铺类型，1-天猫，0-淘宝")
    private String shopType;
    @Schema(description = "店铺名称")
    private String shopName;
    @Schema(description = "淘宝店铺等级")
    private Integer shopLevel;

    @Schema(description = "商品详情图（需要做适配）")
    private String detailPics;
    @Schema(description = "淘宝轮播图")
    private String imgs;
    @Schema(description = "相关商品图")
    private String reimgs;
    @Schema(description = "店铺logo")
    private String shopLogo;
    @Schema(description = "24小时销量")
    private Long sales24h;
    @Schema(description = "商品是否已经验货，0-否；1-是")
    private String inspectedGoods;
}
