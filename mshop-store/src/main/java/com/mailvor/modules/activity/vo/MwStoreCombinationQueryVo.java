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
import java.util.List;

/**
 * <p>
 * 拼团产品表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-11-19
 */
@Data
@Schema(description = "拼团产品表查询参数")
public class MwStoreCombinationQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "商品id")
    private Long productId;

    @Schema(description = "商户id")
    private Integer merId;

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

    @Schema(description = "活动标题")
    private String title;


    @Schema(description = "参团人数")
    private Integer people;

    @Schema(description = "简介")
    private String info;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "价格")
    private BigDecimal price;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "商品价格")
    private BigDecimal productPrice;


    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "库存")
    private Integer stock;

    private Integer combination;

    @Schema(description = "是否包邮1是0否")
    private Integer isPostage;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(description = "邮费")
    private BigDecimal postage;

    @Schema(description = "拼团内容")
    private String description;

    @Schema(description = "拼团订单有效时间")
    private Integer effectiveTime;

    @Schema(description = "浏览量")
    private Integer browse;

    @Schema(description = "单位名")
    private String unitName;

    /** 是否单独分佣 */
    @JsonProperty("is_sub")
    private Integer isSub;


}
