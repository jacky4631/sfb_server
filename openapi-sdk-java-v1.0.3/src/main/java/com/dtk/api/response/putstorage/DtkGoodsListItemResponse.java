package com.dtk.api.response.putstorage;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品列表响应结果实体
 *
 * @author 1
 * @date 2020/11/10 15:54
 */
@Getter
@Setter
public class DtkGoodsListItemResponse extends DtkBaseItemResponse {

    @ApiModelProperty(value = "淘宝加密商品id")
    private String goodsSign;
    @ApiModelProperty(value = "定金，若无定金，则显示0")
    private BigDecimal quanMLink;
    @ApiModelProperty(value = "立减，若无立减金额，则显示0")
    private BigDecimal hzQuanOver;
    @ApiModelProperty(value = "0.不包运费险 1.包运费险")
    private Integer yunfeixian;
    @ApiModelProperty(value = "预估淘礼金")
    private BigDecimal estimateAmount;
    @ApiModelProperty(value = "店铺logo")
    private String shopLogo;
    @ApiModelProperty(value = "特色文案 如：买一送一、第二件0元等")
    private List<String> specialText;
    @ApiModelProperty(value = "偏远地区包邮，1-包邮，0-否")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty(value = "商品视频")
    private String video;
    @ApiModelProperty(value = "定向佣金类型，1非定向，3定向（12.22新增字段）")
    private Integer directCommissionType;
    @ApiModelProperty(value = "定向佣金（12.22新增字段）")
    private Integer directCommission;
    @ApiModelProperty(value = "定向链接（12.22新增字段）")
    private String directCommissionLink;
    @ApiModelProperty(value = "1.购物津贴；2.跨店满减；0.无")
    private Integer discountType;
    @ApiModelProperty(value = "活动满减的满值")
    private BigDecimal discountFull;
    @ApiModelProperty(value = "活动满减的减值")
    private BigDecimal discountCut;
    @ApiModelProperty(value = "单单有奖活动id")
    private String activityId;
    @ApiModelProperty(value = "单单有奖金额")
    private BigDecimal cpaRewardAmount;
    @ApiModelProperty(value = "后台配置的活动id")
    private List<Integer> marketGroup;
    @ApiModelProperty(value = "活动信息")
    private List<Activity> activityInfo;
    @ApiModelProperty(value = "是否已经验货，0-否；1-是")
    private Integer inspectedGoods;

    @Data
    public static class Activity {
        private Integer activityId;
        private String activityName;
    }
}
