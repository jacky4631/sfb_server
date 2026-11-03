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
public class GoodsParseOrigInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品标题")
    private String title;
    @Schema(description = "店铺名")
    private String shopName;
    @Schema(description = "店铺LOGO图")
    private String shopLogo;

    @Schema(description = "商品主图")
    private String image;
    @Schema(description = "券开始时间")
    private String startTime;

    @Schema(description = "券结束时间")
    private String endTime;
    @Schema(description = "券金额")
    private String amount;
    @Schema(description = "券门槛金额")
    private String startFee;

    @Schema(description = "商品价格")
    private String price;
    @Schema(description = "券ID")
    private String activityId;
    @Schema(description = "PID")
    private String pid;
    @Schema(description = "券状态。0:可用; 非0:不可用")
    private String status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStartFee() {
        return startFee;
    }

    public void setStartFee(String startFee) {
        this.startFee = startFee;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
