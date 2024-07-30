package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 */
@Data
@ApiModel(value = "ParseContentVo", description = "淘口令")
public class GoodsParseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "淘宝商品ID")
    private String goodsId;
    @ApiModelProperty(value = "链接")
    private String originUrl;
    @ApiModelProperty(value = "链接中的信息类型")
    private String originType;
    @ApiModelProperty(value = "当dataType=goods时，标识商品ID；当dataType=activity时，标识活动会场id")
    private String itemId;
    @ApiModelProperty(value = "当dataType=goods时，标识商品名称；当dataType=activity时，标识活动会场名称")
    private String itemName;
    @ApiModelProperty(value = "当dataType=goods时，标识商品主图；当dataType=activity时，标识活动会场主图；")
    private String mainPic;
    @ApiModelProperty(value = "goods标识商品；activity标识活动会场")
    private String dataType;
    @ApiModelProperty(value = "优惠券类型，0-全网公开券；1-阿里妈妈券")
    private String couponSrcScene;
    @ApiModelProperty(value = "商品链接")
    private String itemLink;
    @ApiModelProperty(value = "优惠券链接")
    private String couponLink;

    private GoodsParseOrigInfoVo originInfo;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCouponSrcScene() {
        return couponSrcScene;
    }

    public void setCouponSrcScene(String couponSrcScene) {
        this.couponSrcScene = couponSrcScene;
    }

    public String getItemLink() {
        return itemLink;
    }

    public void setItemLink(String itemLink) {
        this.itemLink = itemLink;
    }

    public String getCouponLink() {
        return couponLink;
    }

    public void setCouponLink(String couponLink) {
        this.couponLink = couponLink;
    }

    public GoodsParseOrigInfoVo getOriginInfo() {
        return originInfo;
    }

    public void setOriginInfo(GoodsParseOrigInfoVo originInfo) {
        this.originInfo = originInfo;
    }
}
