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
@Schema(description = "淘口令")
public class GoodsWordVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品优惠券推广链接")
    private String couponClickUrl;

    @Schema(description = "优惠券结束时间")
    private String couponEndTime;

    @Schema(description = "优惠券面额")
    private String couponInfo;

    @Schema(description = "优惠券开始时间")
    private String couponStartTime;

    @Schema(description = "商品id")
    private Long itemId;

    @Schema(description = "优惠券总量")
    private Long couponTotalCount;

    @Schema(description = "优惠券剩余量")
    private Long couponRemainCount;

    @Schema(description = "商品淘客链接")
    private String itemUrl;

    @Schema(description = "淘口令")
    private String tpwd;

    @Schema(description = "针对iOS14版本，增加对应能解析的长口令")
    private String longTpwd;

    @Schema(description = "佣金比例")
    private String maxCommissionRate;

    @Schema(description = "短链接")
    private String shortUrl;

    @Schema(description = "当传入请求参数channelId、specialId、externalId时，该字段展示预估最低佣金率(%)")
    private String minCommissionRate;

    @Schema(description = "商品的快站链接（支持在微信端直接访问商品详情）")
    private String kuaiZhanUrl;

    @Schema(description = "商品原价")
    private String originalPrice;

    @Schema(description = "券后价")
    private String actualPrice;


}
