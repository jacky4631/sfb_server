package com.mailvor.modules.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.serializer.DoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 购物车表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-25
 */
@Data
@Schema(description = "购物车表查询参数")
public class MwStoreCartQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车表ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品属性")
    private String productAttrUnique;

    @Schema(description = "商品数量")
    private Integer cartNum;


    @Schema(description = "拼团id")
    private Long combinationId;

    @Schema(description = "秒杀产品ID")
    private Long seckillId;

    @Schema(description = "砍价id")
    private Long bargainId;

    @Schema(description = "商品信息")
    private MwStoreProductQueryVo productInfo;

    @Schema(description = "成本价")
    @JsonSerialize(using = DoubleSerializer.class)
    private Double costPrice;

    @Schema(description = "真实价格")
    @JsonSerialize(using = DoubleSerializer.class)
    private Double truePrice;

    @Schema(description = "真实库存")
    private Integer trueStock;

    @JsonSerialize(using = DoubleSerializer.class)
    @Schema(description = "vip真实价格")
    private Double vipTruePrice;

    @Schema(description = "唯一id")
    private String unique;

    @Schema(description = "是否评价")
    private Long isReply;

}
