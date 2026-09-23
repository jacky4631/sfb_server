package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 我发布的商品响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:07
 */
@Getter
@Setter
public class DtkGetOwnerGoodsItemResponse {
    @ApiModelProperty(value = "商品id")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;
    @ApiModelProperty(value = "上架时间")
    private String createTime;
    @ApiModelProperty(value = "下架时间")
    private String lowerShelfTime;
    @ApiModelProperty(value = "拒绝理由/下架理由，无上架时间则是拒绝理由，有上架时间则是下架理由")
    private String refuseReason;
    @ApiModelProperty(value = "消费点券")
    private String consumptionCoupon;
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "备注")
    private String note;
    @ApiModelProperty(value = "提交时间")
    private String shelfTime;
    @ApiModelProperty(value = "销量")
    private Integer monthSales;
    @ApiModelProperty(value = "佣金比例")
    private BigDecimal commissionRate;
    @ApiModelProperty(value = "领券量")
    private BigDecimal couponReceiveNum;

}
