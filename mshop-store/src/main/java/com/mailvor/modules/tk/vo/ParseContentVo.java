package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author shenji
 * @date 2019-10-19
 */
@Data
@Schema(description = "粘贴板识别参数")
public class ParseContentVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "数据解析状态：0：无口令，无链接，不含敏感词--> APP端弹窗文本搜索，原始文本作为搜索词；1：无口令，无链接，有敏感词-->APP不弹窗；2：有口令或链接，未解析出商品ID，或解析出商品ID但联盟无信息-->APP弹窗：未找到该商品3：有口令或链接，解析出商品ID，且联盟有商品信息-->APP弹窗，展示该商品信息4：有口令或链接，解析出活动ID，没有详情信息-->APP弹窗，不展示该活动信息5：有口令或链接，解析出活动ID，有详情信息-->APP弹窗，展示活动信息")
    private Integer parseStatus;

    @Schema(description = "原始文本")
    private String originContent;

    @Schema(description = "解析出的商品所属平台：taobao：淘宝jd：京东pdd：拼多多")
    private String platType;

    @Schema(description = "dataType=goods时，商品id（拼多多可能是GoodsSign、goodsId）dataType=activity时，活动id")
    private String itemId;

    @Schema(description = "dataType=goods时，商品链接dataType=activity时，活动链接")
    private String itemLink;

    @Schema(description = "dataType=goods时，商品名称dataType=activity时，活动名称")
    private String itemName;

    @Schema(description = "商品主图 或 活动主图")
    private String mainPic;

    @Schema(description = "商品营销图")
    private String marketMainPic;

    @Schema(description = "月销量")
    private Long monthSales;

    @Schema(description = "商品原价")
    private Double originalPrice;
    @Schema(description = "券后价")
    private Double actualPrice;
    @Schema(description = "佣金比例")
    private Double commissionRate;
    @Schema(description = "预估佣金金额")
    private Double commissionAmount;
    @Schema(description = "预估佣金金额")
    private Double couponPrice;

    @Schema(description = "领券链接地址")
    private String couponLink;

    @Schema(description = "数据类型：goods-商品类型；activity-活动类型；")
    private String dataType;
    @Schema(description = "淘口令")
    private String tpwd;
    @Schema(description = "长口令")
    private String longTpwd;
    @Schema(description = "dataType=goods时，商品推广链接dataType=activity时，活动推广链接")
    private String promotionShortUrl;

    private Integer integral;

    private String shopName;
}
