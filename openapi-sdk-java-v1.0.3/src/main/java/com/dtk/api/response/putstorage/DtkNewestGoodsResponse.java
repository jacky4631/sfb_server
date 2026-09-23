package com.dtk.api.response.putstorage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品更新响应结果实体
 *
 * @author 1
 * @date 2020/11/10 16:21
 */
@Getter
@Setter
public class DtkNewestGoodsResponse {
    @ApiModelProperty(value = "商品id")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;
    @ApiModelProperty(value = "淘宝加密商品id")
    private String goodsSign;
    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;
    @ApiModelProperty(value = "实际价格")
    private BigDecimal actualPrice;
    @ApiModelProperty(value = "30天销量")
    private BigDecimal monthSales;
    @ApiModelProperty(value = "今日销量")
    private BigDecimal dailySales;
    @ApiModelProperty(value = "佣金类型， 0-通用 ，1- 定向，2-高佣，3-营销计划")
    private Integer commissionType;
    @ApiModelProperty(value = "优惠券领券量")
    private Integer couponReceiveNum;
    @ApiModelProperty(value = "券金额")
    private BigDecimal couponPrice;
    @ApiModelProperty(value = "折扣力度")
    private BigDecimal discounts;
    @ApiModelProperty(value = "佣金比率")
    private BigDecimal commissionRate;
    @ApiModelProperty(value = "热推值")
    private BigDecimal hotPush;
    @ApiModelProperty(value = "商品在大淘客的二级分类id，该分类为前端分类，一个商品可能有多个二级分类id")
    private List<Integer> subcid;
    @ApiModelProperty(value = "两小时销量")
    private BigDecimal twoHoursSales;
    @ApiModelProperty(value = "优惠券id")
    private String couponId;
    @ApiModelProperty(value = "优惠券链接")
    private String couponLink;
    @ApiModelProperty(value = "优惠券剩余量")
    private Integer couponRemainCount;
    @ApiModelProperty(value = "特色文案")
    private List<String> specialText;
    @ApiModelProperty(value = "是否已经验货，0-否；1-是")
    private Integer inspectedGoods;
}
