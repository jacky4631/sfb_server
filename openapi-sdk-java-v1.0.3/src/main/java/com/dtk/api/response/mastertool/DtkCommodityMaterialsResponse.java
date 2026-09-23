package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/7
 * @time: 16:48
 */
@Getter
@Setter
public class DtkCommodityMaterialsResponse {
    @ApiModelProperty("采集群头像")
    private String headImg;
    @ApiModelProperty("采集群名称")
    private String name;
    @ApiModelProperty("是否新推 0-不是 1是")
    private Integer newest;
    @ApiModelProperty("是否热门 0-不是 1是")
    private Integer hot;
    @ApiModelProperty("推广时间")
    private String createTime;
    @ApiModelProperty("精推素材列表")
    private List<MaterialInfo> materialList;

    @Data
    public static class MaterialInfo {
        @ApiModelProperty("素材类型 1.文本 2.图片 3.视频")
        private Integer type;
        @ApiModelProperty("精推素材")
        private String content;
    }
}
