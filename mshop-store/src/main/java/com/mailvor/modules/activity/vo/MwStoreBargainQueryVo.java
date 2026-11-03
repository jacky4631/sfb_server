package com.mailvor.modules.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 砍价表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-21
 */
@Data
@Schema(description = "砍价表查询参数")
public class MwStoreBargainQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "砍价产品ID")
    private Long id;

    @Schema(description = "关联产品ID")
    private Long productId;

    @Schema(description = "砍价活动名称")
    private String title;

    @Schema(description = "砍价活动图片")
    private String image;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "砍价产品轮播图")
    private String images;

    @Schema(description = "砍价开启时间")
    private Date startTime;

    @Schema(description = "砍价结束时间")
    private Date stopTime;

    @Schema(description = "砍价产品名称")
    private String storeName;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "砍价金额")
    private BigDecimal price;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "砍价商品最低价")
    private BigDecimal minPrice;

    @Schema(description = "每次购买的砍价产品数量")
    private Integer num;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "用户每次砍价的最大金额")
    private BigDecimal bargainMaxPrice;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "用户每次砍价的最小金额")
    private BigDecimal bargainMinPrice;

    @Schema(description = "用户每次砍价的次数")
    private Integer bargainNum;

    @Schema(description = "砍价状态 0(到砍价时间不自动开启)  1(到砍价时间自动开启时间)")
    private Integer status;

    @Schema(description = "砍价详情")
    private String description;

    @Schema(description = "反多少积分")
    private BigDecimal giveIntegral;

    @Schema(description = "砍价活动简介")
    private String info;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "成本价")
    private BigDecimal cost;

    @Schema(description = "排序")
    private Integer sort;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "邮费")
    private BigDecimal postage;

    @Schema(description = "砍价规则")
    private String rule;

    @Schema(description = "砍价产品浏览量")
    private Integer look;

    @Schema(description = "砍价产品分享量")
    private Integer share;

    @Schema(description = "砍价产品参与人数")
    private Long people;


}
