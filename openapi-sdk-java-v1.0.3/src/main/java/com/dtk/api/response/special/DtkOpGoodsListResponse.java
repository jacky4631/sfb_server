package com.dtk.api.response.special;

import com.dtk.api.response.putstorage.DtkBaseItemResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 9.9包邮精选响应结果实体
 *
 * @author baige
 * @date 2020/11/12 11:16
 */
@Getter
@Setter
public class DtkOpGoodsListResponse extends DtkBaseItemResponse {
    @ApiModelProperty(value = "精选分类")
    private Integer nineCid;
    @ApiModelProperty(value = "定金，若无定金，则显示0")
    private BigDecimal quanMLink;
    @ApiModelProperty(value = "立减，若无立减金额，则显示0")
    private BigDecimal hzQuanOver;
    @ApiModelProperty(value = "0.不包运费险 1.包运费险")
    private Integer yunfeixian;
    @ApiModelProperty(value = "预估淘礼金")
    private BigDecimal estimateAmount;
    @ApiModelProperty(value = "偏远地区包邮，0.不包邮，1.包邮")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty(value = "商品视频")
    private String video;
    private Integer tchaosi;
    private Integer haitao;
    private List<String> specialText;
    private List<Integer> marketGroup;
    private Integer lowest;
}
