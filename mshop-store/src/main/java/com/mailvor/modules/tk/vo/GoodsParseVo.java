package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 */
@Data
@Schema(description = "淘口令")
public class GoodsParseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "淘宝商品ID")
    private String goodsId;
    @Schema(description = "链接")
    private String originUrl;
    @Schema(description = "链接中的信息类型")
    private String originType;
    @Schema(description = "当dataType=goods时，标识商品ID；当dataType=activity时，标识活动会场id")
    private String itemId;
    @Schema(description = "当dataType=goods时，标识商品名称；当dataType=activity时，标识活动会场名称")
    private String itemName;
    @Schema(description = "当dataType=goods时，标识商品主图；当dataType=activity时，标识活动会场主图；")
    private String mainPic;
    @Schema(description = "goods标识商品；activity标识活动会场")
    private String dataType;
    @Schema(description = "优惠券类型，0-全网公开券；1-阿里妈妈券")
    private String couponSrcScene;
    @Schema(description = "商品链接")
    private String itemLink;
    @Schema(description = "优惠券链接")
    private String couponLink;
    @Schema(description = "预估到手价")
    private Double realPostFee;
    @Schema(description = "佣金比例")
    private String commissionRate;
    @Schema(description = "短链接")
    private String shortUrl;
    @Schema(description = "短链接")
    private String shortTpwd;
    @Schema(description = "cps长链接")
    private String cpsLongUrl;
    @Schema(description = "cps全链路淘口令")
    private String cpsFullTpwd;

    private GoodsParseOrigInfoVo originInfo;

}
