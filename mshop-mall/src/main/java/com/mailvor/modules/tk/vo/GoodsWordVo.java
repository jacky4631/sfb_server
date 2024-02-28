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
@ApiModel(value = "GoodsWordVo", description = "淘口令")
public class GoodsWordVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品优惠券推广链接")
    private String couponClickUrl;

    @ApiModelProperty(value = "优惠券结束时间")
    private String couponEndTime;

    @ApiModelProperty(value = "优惠券面额")
    private String couponInfo;

    @ApiModelProperty(value = "优惠券开始时间")
    private String couponStartTime;

    @ApiModelProperty(value = "商品id")
    private Long itemId;

    @ApiModelProperty(value = "优惠券总量")
    private Long couponTotalCount;

    @ApiModelProperty(value = "优惠券剩余量")
    private Long couponRemainCount;

    @ApiModelProperty(value = "商品淘客链接")
    private String itemUrl;

    @ApiModelProperty(value = "淘口令")
    private String tpwd;

    @ApiModelProperty(value = "针对iOS14版本，增加对应能解析的长口令")
    private String longTpwd;

    @ApiModelProperty(value = "佣金比例")
    private String maxCommissionRate;

    @ApiModelProperty(value = "短链接")
    private String shortUrl;

    @ApiModelProperty(value = "当传入请求参数channelId、specialId、externalId时，该字段展示预估最低佣金率(%)")
    private String minCommissionRate;

    @ApiModelProperty(value = "商品的快站链接（支持在微信端直接访问商品详情）")
    private String kuaiZhanUrl;

    @ApiModelProperty(value = "商品原价")
    private String originalPrice;

    @ApiModelProperty(value = "券后价")
    private String actualPrice;


}
