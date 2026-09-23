package com.dtk.api.response.special;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 精选专题响应结果实体
 *
 * @author baige
 * @date 2020/11/12 15:54
 */
@Data
public class DtkCatalogueResponse {
    @ApiModelProperty(value = "活动ID")
    private Long topicId;
    @ApiModelProperty(value = "活动名称")
    private String topicName;
    @ApiModelProperty(value = "活动开始时间")
    private String startTime;
    @ApiModelProperty(value = "活动结束时间")
    private String endTime;
    @ApiModelProperty(value = "专题宣传图，用于首页banner或者其他资源位的展示。如果有两张，分别用于pc端和移动端。请参照尺寸适配")
    private List<String> banner;
    @ApiModelProperty(value = "专题活动详情内顶部图片，如果有两张，分别用于pc端和移动端。请参照尺寸适配")
    private List<String> topBanner;
}
