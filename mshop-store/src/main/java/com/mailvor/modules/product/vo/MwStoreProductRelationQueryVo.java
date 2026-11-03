package com.mailvor.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.serializer.DoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商品点赞和收藏表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-10-23
 */
@Data
@Schema(description = "商品点赞和收藏表查询参数")
public class MwStoreProductRelationQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "商品ID")
    private String productId;
    @Schema(description = "商品ID")
    private String originalProductId;

    @Schema(description = "类型(收藏(collect）、点赞(like))")
    private String type;

    @Schema(description = "某种类型的商品(普通商品、秒杀商品)")
    private String category;

    @Schema(description = "添加时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "商品图片地址")
    private String img;
    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "原始价格")
    private String startPrice;
    @Schema(description = "真实价格")
    private String endPrice;

}
