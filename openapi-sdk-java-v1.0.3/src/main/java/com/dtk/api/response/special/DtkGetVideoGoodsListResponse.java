package com.dtk.api.response.special;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 短视频商品响应结果实体
 *
 * @author baige
 * @date 2020/11/30 17:02
 */
@Data
public class DtkGetVideoGoodsListResponse {
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "短标题")
    private String dTitle;

    @ApiModelProperty(value = "封面图片")
    private String mainPic;

    @ApiModelProperty(value = "月销量")
    private Integer mothSales;

    @ApiModelProperty(value = "今日销量")
    private Integer dailySales;

    @ApiModelProperty(value = "优惠券id")
    private String couponId;

    @ApiModelProperty(value = "优惠券面额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "原价")
    private BigDecimal originPrice;

    @ApiModelProperty(value = "券后价")
    private BigDecimal actualPrice;

    @ApiModelProperty(value = "优惠券总量")
    private Integer couponTotalNum;

    @ApiModelProperty(value = "优惠券剩余量")
    private Integer couponRemainNum;

    @ApiModelProperty(value = "优惠券领取量")
    private Integer couponReceiveNum;

    @ApiModelProperty(value = "优惠券地址")
    private String couponLink;

    @ApiModelProperty(value = "优惠券开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponStartTime;

    @ApiModelProperty(value = "优惠券结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date couponEndTime;

    @ApiModelProperty(value = "分佣比例")
    private BigDecimal commissionRate;

    @ApiModelProperty(value = "视频地址")
    private String videoUrl;

    @ApiModelProperty(value = "视频封面图")
    private String videoCoverImg;

    @ApiModelProperty(value = "点赞数")
    private Integer videoLikeCount;

    @ApiModelProperty(value = "评论数")
    private Integer videoCommentCount;

    @ApiModelProperty(value = "分享数")
    private Integer videoShareCount;

    @ApiModelProperty(value = "视频第一针动态图")
    private String videoDynamicCoverImg;

    public String getdTitle() {
        return dTitle;
    }

    public void setdTitle(String dTitle) {
        this.dTitle = dTitle;
    }
}
