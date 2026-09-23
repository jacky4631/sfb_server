package com.dtk.api.response.special;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 品牌库响应结果实体
 *
 * @author baige
 * @date 2020/11/12 16:08
 */
@Data
public class DtkGetBrandListResponse {
    @ApiModelProperty("品牌Id")
    private Long brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("品牌logo")
    private String brandLogo;

    private String brandEnglish;

    private List<ShopVO> shop;

    private Integer brandScore;

    private String location;

    private String establishTime;

    private String belongTo;

    private String position;

    private String consumer;

    private String label;

    @ApiModelProperty("一句话标签")
    private String simpleLabel;

    @ApiModelProperty("主营类目")
    private String cids;

    @ApiModelProperty("近期销量 两小时销量")
    private Integer sales2h;

    @ApiModelProperty("粉丝数")
    private Integer fansNum;

    @ApiModelProperty("品牌介绍")
    private String brandDesc;

    @Data
    public static class ShopVO {
        private String name;

        private String sellerId;
    }
}
