package com.dtk.api.response.special;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 品牌栏目响应结果实体
 *
 * @author baige
 * @date 2020/11/30 17:28
 */
@Data
public class DtkGetColumnListResponse {
    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "品牌logo")
    private String brandLogo;

    @ApiModelProperty(value = "品牌一句话描述")
    private String brandFeatures;

    @ApiModelProperty(value = "近期销量")
    private Integer sales;

    @ApiModelProperty(value = "最大优惠券金额")
    private BigDecimal maxDiscountAmount;

    @ApiModelProperty(value = "最大折扣")
    private BigDecimal maxDiscount;

    @ApiModelProperty(value = "品牌热推商品")
    private List<BrandHotGoodsResponse> goodsList;

    @Data
    public static class BrandHotGoodsResponse {
        @ApiModelProperty(value = "商品id")
        private Integer id;

        @ApiModelProperty(value = "淘宝商品id")
        private String goodsId;

        @ApiModelProperty(value = "大淘客分类id")
        private Integer cid;

        @ApiModelProperty(value = "品牌id")
        private String brandId;

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

        public String getdTitle() {
            return dTitle;
        }

        public void setdTitle(String dTitle) {
            this.dTitle = dTitle;
        }
    }
}
