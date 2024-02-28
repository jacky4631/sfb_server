package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "MwGoodsQueryVo对象", description = "商品表查询参数")
public class GoodsDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Long id;

    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;

    @ApiModelProperty(value = "商品淘宝链接")
    private String itemLink;

    @ApiModelProperty(value = "淘宝标题")
    private String title;

    @ApiModelProperty(value = "短标题")
    private String dtitle;

    @ApiModelProperty(value = "特色文案（商品卖点）")
    private String specialText;

    @ApiModelProperty(value = "券后价")
    private Double actualPrice;

    @ApiModelProperty(value = "优惠券金额")
    private Double couponPrice;

    @ApiModelProperty(value = "月销量")
    private Long monthSales;

    @ApiModelProperty(value = "商品图")
    private String mainPic;

    @ApiModelProperty(value = "商品描述")
    private String desc;

    @ApiModelProperty(value = "商品原价")
    private Double originalPrice;

    @ApiModelProperty(value = "历史低价，0.不是，1.史低")
    private Integer lowest;

    @ApiModelProperty(value = "营销主图链接")
    private String marketingMainPic;

    @ApiModelProperty(value = "商品视频")
    private String video;
    @ApiModelProperty(value = "佣金比例")
    private Double commissionRate;
    @ApiModelProperty(value = "优惠券链接")
    private String couponLink;
    @ApiModelProperty(value = "券总量")
    private Long couponTotalNum;
    @ApiModelProperty(value = "领券量")
    private Long couponReceiveNum;
    @ApiModelProperty(value = "优惠券结束时间")
    private String couponEndTime;
    @ApiModelProperty(value = "优惠券开始时间")
    private String couponStartTime;
    @ApiModelProperty(value = "优惠券使用条件")
    private String couponConditions;

    @ApiModelProperty(value = "2小时销量")
    private Long twoHoursSales;
    @ApiModelProperty(value = "当天销量")
    private Long dailySales;
    @ApiModelProperty(value = "是否是品牌商品")
    private Long brand;
    @ApiModelProperty(value = "品牌id")
    private Long brandId;
    @ApiModelProperty(value = "品牌名称")
    private String brandName;
    @ApiModelProperty(value = "店铺类型，1-天猫，0-淘宝")
    private String shopType;
    @ApiModelProperty(value = "店铺名称")
    private String shopName;
    @ApiModelProperty(value = "淘宝店铺等级")
    private Integer shopLevel;

    @ApiModelProperty(value = "商品详情图（需要做适配）")
    private String detailPics;
    @ApiModelProperty(value = "淘宝轮播图")
    private String imgs;
    @ApiModelProperty(value = "相关商品图")
    private String reimgs;
    @ApiModelProperty(value = "店铺logo")
    private String shopLogo;
    @ApiModelProperty(value = "24小时销量")
    private Long sales24h;
    @ApiModelProperty(value = "商品是否已经验货，0-否；1-是")
    private String inspectedGoods;
}
