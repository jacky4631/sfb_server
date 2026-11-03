package com.mailvor.modules.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import cn.hutool.core.util.StrUtil;
import com.mailvor.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品秒杀产品表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-14
 */
@Data
@Schema(description = "商品秒杀产品表查询参数")
public class MwStoreSeckillQueryVo implements Serializable{
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品秒杀产品表id")
    private Long id;

    @Schema(description = "商品id")
    private Long productId;

    @Schema(description = "推荐图")
    private String image;

    @Schema(description = "轮播图")
    private String images;

    private List<String> sliderImageArr;

    public List<String> getSliderImageArr() {
        if(StrUtil.isNotEmpty(images)){
            return Arrays.asList(images.split(","));
        }else {
            return new ArrayList<>();
        }

    }

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "价格")
    private BigDecimal price;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "成本")
    private BigDecimal cost;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "原价")
    private BigDecimal otPrice;

    @Schema(description = "轮播图")
    private String[] pics;
    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "简介")
    private String info;

    @Schema(description = "返多少积分")
    private BigDecimal giveIntegral;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "单位名")
    private String unitName;

    @Schema(description = "邮费")
    private BigDecimal postage;

    @Schema(description = "内容")
    private String description;

    @Schema(description = "开始时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date stopTime;

    @Schema(description = "产品状态")
    private Integer status;

    @Schema(description = "最多秒杀几个")
    private Integer num;

    @Schema(description = "显示")
    private Integer isShow;

    @Schema(description = "百分比")
    private Integer percent;

    /** 是否单独分佣 */
    @JsonProperty("is_sub")
    private Integer isSub;



}
