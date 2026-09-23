package com.dtk.api.response.mastertool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 商品评论响应结果实体
 *
 * @author 1
 * @date 2020/11/30 10:32
 */
@Data
public class DtkGoodsCommentListResponse {
    @ApiModelProperty(value = "评论总数")
    private Integer commentCount;

    @ApiModelProperty(value = "追评总数")
    private Integer commentAppendCount;

    @ApiModelProperty(value = "评论列表")
    private List<Feedback> commentList;

    @ApiModelProperty(value = "标签列表")
    private List<Tags> tagList;

    @Data
    public static class Feedback {
        @ApiModelProperty(value = "评论的图片列表")
        private List<String> images;

        @ApiModelProperty(value = "评论内容")
        private String commentContent;

        @ApiModelProperty(value = "评论时间")
        private String commentDate;

        @ApiModelProperty(value = "用户昵称")
        private String userNick;

        @ApiModelProperty(value = "用户头像")
        private String headPicUrl;

        @ApiModelProperty(value = "评论视频")
        private Video video;

        @ApiModelProperty(value = "购买的商品分类规格")
        private Map<String, String> skuMap;

        @ApiModelProperty(value = "间隔天数")
        private String intervalDay;

        @ApiModelProperty(value = "追加评论")
        private Append commentAppend;
    }

    /**
     * 视频
     */
    @Data
    public static class Video {
        @ApiModelProperty(value = "视频封面图片")
        private String coverUrl;

        @ApiModelProperty(value = "视频地址")
        private String videoUrl;
    }

    /**
     * 标签
     */
    @Data
    public static class Tags {
        @ApiModelProperty(value = "标签统计数")
        private Integer count;

        @ApiModelProperty(value = "标签名")
        private String tag;
    }

    /**
     * 评论追加
     */
    @Data
    public static class Append {
        @ApiModelProperty(value = "评论的图片列表")
        private List<String> images;

        @ApiModelProperty(value = "评论内容")
        private String commentContent;

        @ApiModelProperty(value = "间隔天数")
        private Integer intervalDay;
    }
}
