package com.mailvor.modules.tk.vo.pdd;

import lombok.Data;
import java.util.List;

/** Official PDD detail, using the same product fields as the app's list. */
@Data
public class PddGoodsDetailVO {
    private String platform = "pdd";
    private String goodsId;
    private String title;
    private String dtitle;
    private String sales;
    private String shopName;
    private String img;
    private String cateName;
    private Double startPrice;
    private Double endPrice;
    private Double feeRatio;
    private Double fee;
    private List<String> banners;
    private List<String> details;
    private Double coupon;
    private Double couponLimit;
    private String couponStart;
    private String couponEnd;
    private String descScore;
    private String shipScore;
    private String serviceScore;
    private String shopLogo;
    private String sellerId;
    private List<String> label;
}
