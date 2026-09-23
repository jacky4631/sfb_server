package com.dtk.api.response.special;

import com.dtk.api.response.base.DtkPageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 品牌商品列表响应结果实体
 *
 * @author 1
 * @date 2020/11/30 17:44
 */
@Getter
@Setter
public class DtkGetBrandGoodsListResponse extends DtkPageResponse<DtkGetColumnListResponse.BrandHotGoodsResponse> {
    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌logo
     */
    private String brandLogo;

    /**
     * 品牌描述
     */
    private String brandDesc;

    /**
     * 品牌特色（文案：一句话描述）
     */
    private String brandFeatures;

    /**
     * 品牌销量
     */
    private Integer sales;

    /**
     * 品牌粉丝数
     */
    private Integer fansNum;

    @ApiModelProperty(value = "商品id")
    private Integer id;

    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;

    @ApiModelProperty(value = "大淘客分类id")
    private Integer cid;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "短标题")
    private String dTitle;

    @ApiModelProperty(value = "商品描述")
    private String desc;

    @ApiModelProperty(value = "商品特色文案")
    private List<String> specialText;

    @ApiModelProperty(value = "佣金类型：0-通用；1-定向；2-高佣；3-营销计划")
    private Integer commissionType;

    @ApiModelProperty(value = "佣金比例")
    private BigDecimal commissionRate;

    /**
     * 活动类型：1-无活动；2-淘抢购；3-聚划算。
     */
    private Integer activityType;

    /**
     * 今日销量
     */
    private Long dailySales;

    /**
     * 月销量
     */
    private Long monthSales;

    /**
     * 图片地址
     */
    private String mainPic;

    /**
     * 营销主图
     */
    private String marketingMainPic;

    /**
     * 视频地址
     */
    private String video;

    /**
     * 原价
     */
    private BigDecimal originPrice;

    /**
     * 券后价
     */
    private BigDecimal actualPrice;

    /**
     * 优惠券id
     */
    private String couponId;

    /**
     * 券价
     */
    private BigDecimal couponPrice;

    /**
     * 优惠券链接地址
     */
    private String couponLink;

    /**
     * 优惠券使用条件
     */
    private String couponConditions;

    /**
     * 领券量
     */
    private Integer couponReceiveNum;

    /**
     * 优惠券总量
     */
    private Integer couponTotalNum;

    /**
     * 优惠券开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponEndTime;

    /**
     * 优惠券结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponStartTime;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 偏远地区是否包邮
     */
    private Integer freeshipRemoteDistrct;


}
