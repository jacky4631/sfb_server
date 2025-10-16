/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "商品通用vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdKuGoodsShopInfoVO {
    /**
     * {
     *   "data" : [ {
     *     "shop_info" : {
     *       "logisticsLvyueScore" : "3.90",
     *       "scoreRankRate" : "46.97",
     *       "shopLevel" : 4,
     *       "userEvaluateScore" : "4.90",
     *       "afterServiceScore" : "4.70",
     *       "shopLabel" : "0",
     *       "shopName" : "玉莲官方旗舰店",
     *       "afsFactorScoreRankGrade" : "高",
     *       "shopId" : 16427134,
     *       "logisticsFactorScoreRankGrade" : "中",
     *       "commentFactorScoreRankGrade" : "高"
     *     }
     * }
     * */
    @Schema(description = "物流履约评分")
    private String logisticsLvyueScore;

    @Schema(description = "店铺风向标")
    private String scoreRankRate;
    @Schema(description = "用户评价评分")
    private String userEvaluateScore;

    @Schema(description = "售后服务评分")
    private String afterServiceScore;

    @JSONField(name = "shopName")
    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "售后服务评级")
    private String afsFactorScoreRankGrade;


    @Schema(description = "物流履约评级")
    private String logisticsFactorScoreRankGrade;


    @Schema(description = "用户评价评级")
    private String commentFactorScoreRankGrade;

}
