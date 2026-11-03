/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.mailvor.modules.product.service.dto.FromatDetailDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品秒杀产品表
* @author huangyu
* @date 2020-05-13
*/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_store_seckill")
public class MwStoreSeckill extends BaseDomain {

    /** 商品秒杀产品表id */
    @TableId
    @Schema(description = "商品秒杀产品表id")
    private Long id;


    /** 商品id */
    @Schema(description = "商品id")
    private Long productId;


    /** 推荐图 */
    @NotBlank(message = "请上传商品图片")
    @Schema(description = "推荐图")
    private String image;


    /** 轮播图 */
    @NotBlank(message = "请上传商品轮播")
    @Schema(description = "轮播图")
    private String images;


    /** 活动标题 */
    @NotBlank(message = "请填写秒杀名称")
    @Schema(description = "秒杀名称")
    private String title;


    /** 简介 */
    @Schema(description = "简介")
    private String info;


    /** 返多少积分 */
    @Schema(description = "返多少积分")
    private BigDecimal giveIntegral;


    /** 排序 */
    @Schema(description = "排序")
    private Integer sort;


    /** 库存 */
    @Schema(description = "秒杀库存")
    private Integer stock;

    /** 秒杀价 */
    @Schema(description = "秒杀价")
    private BigDecimal price;

    /** 原价 */
    @Schema(description = "原价")
    private BigDecimal otPrice;

    /** 成本 */
    @Schema(description = "成本")
    private BigDecimal cost;
    /** 销量 */
    @Schema(description = "秒杀销量")
    private Integer sales;


    /** 单位名 */
    @Schema(description = "单位名")
    private String unitName;


    /** 邮费 */
    @Schema(description = "邮费")
    private BigDecimal postage;




    /** 内容 */
    @NotBlank(message = "请填写详情")
    @Schema(description = "详细内容")
    private String description;


    /** 开始时间 */
    @NotNull(message = "请选择秒杀开始时间")
    @Schema(description = "秒杀开始时间")
    private Date startTime;


    /** 结束时间 */
    @NotNull(message = "请选择秒杀结束时间")
    @Schema(description = "秒杀结束时间")
    private Date stopTime;



    /** 产品状态 */
    @Schema(description = "产品状态")
    private Integer status;


    /** 是否包邮 */
    @Schema(description = "是否包邮")
    private Integer isPostage;


    /** 热门推荐 */
    @Schema(description = "是否热门推荐")
    private Integer isHot;



    /** 最多秒杀几个 */
    @NotNull(message = "请输入限购")
    @Min(message = "限购不能小于0",value = 1)
    @Schema(description = "最多秒杀几个商品")
    private Integer num;


    /** 显示 */
    @Schema(description = "是否显示 0否 1显示")
    private Integer isShow;


    /** 时间段id */
    @NotNull(message = "请选择开始时间")
    @Schema(description = "时间段id")
    private Integer timeId;

    /**
     * 规格 0单规格 1多规格
     */
    @Schema(description = "规格 0单规格 1多规格")
    private Integer specType;

    /** 运费模板ID */
    @JsonProperty("temp_id")
    @Schema(description = "运费模板ID")
    private Long tempId;


    /** 属性项目 */
    @TableField(exist = false)
    @Schema(description = "属性项目")
    private List<FromatDetailDto> items;

    /** sku结果集 */
    @TableField(exist = false)
    @Schema(description = "sku结果集")
    private List<Map<String,Object>> attrs;

    public void copy(MwStoreSeckill source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
