package com.dtk.api.response.putstorage;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author:YSH
 * @date: 2021/8/24
 * @time: 14:20
 */
@Getter
@Setter
public class DtkPddGoodsDetailsResponse {
    private Integer categoryId;
    private String categoryName;
    private List<String> goodsGalleryUrls;
    private Long goodsId;
    private List<String> catIds;
    private BigDecimal couponDiscount;
    private String couponEndTime;
    private BigDecimal couponMinOrderAmount;
    private Integer couponRemainQuantity;
    private String couponStartTime;
    private Integer couponTotalQuantity;
    private String descTxt;
    private String goodsDesc;
    private String goodsImageUrl;
    private String goodsName;
    private String goodsSign;
    private String goodsThumbnailUrl;
    private Boolean hasCoupon;
    private Boolean hasMallCoupon;
    private String lgstTxt;
    private Integer mallCouponDiscountPct;
    private String mallCouponEndTime;
    private BigDecimal mallCouponMaxDiscountAmount;
    private BigDecimal mallCouponMinOrderAmount;
    private Integer mallCouponRemainQuantity;
    private String mallCouponStartTime;
    private Integer mallCouponTotalQuantity;
    private Integer mallCps;
    private Integer mallId;
    private String mallName;
    private Integer merchantType;
    private BigDecimal minGroupPrice;
    private BigDecimal minNormalPrice;
    private Boolean onlySceneAuth;
    private Integer optId;
    private List<Integer> optIds;
    private String optName;
    private Integer planType;
    private BigDecimal predictPromotionRate;
    private BigDecimal promotionRate;
    private String salesTip;
    private List<Integer> serviceTags;
    private String servTxt;
    private Integer shareRate;
    private List<String> unifiedTags;
    private List<String> videoUrls;
    private Integer zsDuoId;
    private Integer lowest;
    private Integer salesTipFuzz;

}
