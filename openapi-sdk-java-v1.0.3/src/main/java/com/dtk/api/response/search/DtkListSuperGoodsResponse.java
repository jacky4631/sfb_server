package com.dtk.api.response.search;

import com.dtk.api.response.putstorage.DtkBaseItemResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 超级搜索响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:17
 */
@Getter
@Setter
public class DtkListSuperGoodsResponse extends DtkBaseItemResponse {
    @ApiModelProperty(value = "淘宝加密商品id")
    private String goodsSign;
    @ApiModelProperty(value = "定金，若无定金，则显示0")
    private BigDecimal quanMLink;
    @ApiModelProperty(value = "立减，若无立减金额，则显示0")
    private BigDecimal hzQuanOver;
    @ApiModelProperty(value = "0.不包运费险 1.包运费险")
    private Integer yunfeixian;
    @ApiModelProperty(value = "预估淘礼金")
    private BigDecimal estimateAmount;
    @ApiModelProperty(value = "商品信息-商品小图列表(2021/15新增字段)")
    private List<String> smallImages;
    @ApiModelProperty(value = "特色文案（2021/6/25新增字段）")
    private List<String> specialText;
    private List<BrandInfo> brandList;
    @ApiModelProperty(value = "1.购物津贴；2.跨店满减；0.无")
    private Integer discountType;
    @ApiModelProperty(value = "活动满减的满值")
    private BigDecimal discountFull;
    @ApiModelProperty(value = "活动满减的减值")
    private BigDecimal discountCut;
    @ApiModelProperty(value = "后台配置的活动id")
    private List<Integer> marketGroup;
    @ApiModelProperty(value = "活动信息")
    private List<Activity> activityInfo;
    @ApiModelProperty(value = "单单有奖活动id")
    private String activityId;
    @ApiModelProperty(value = "单单有奖金额")
    private BigDecimal cpaRewardAmount;
    @ApiModelProperty(value = "是否已经验货，0-否；1-是")
    private Integer inspectedGoods;
    @ApiModelProperty(value = "历史最低价")
    private BigDecimal lowest;

    @Data
    public static class BrandInfo {
        private Integer brandId;
        private Integer brandLogo;
        private Integer brandName;
    }

    @Data
    public static class Activity {
        private Integer activityId;
        private String activityName;
    }
}
