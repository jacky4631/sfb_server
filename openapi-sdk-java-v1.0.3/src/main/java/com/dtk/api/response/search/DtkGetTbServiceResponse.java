package com.dtk.api.response.search;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 联盟搜索响应结果实体
 *
 * @author 1
 * @date 2020/11/11 17:33
 */
@Getter
@Setter
public class DtkGetTbServiceResponse {
    private String title;
    private Integer volume;
    private String nick;
    @JsonAlias("coupon_start_time")
    private String couponStartTime;
    @JsonAlias("coupon_end_time")
    private String couponEndTime;
    @JsonAlias("tk_total_sales")
    private String tkTotalSales;
    @JsonAlias("coupon_id")
    private String couponId;
    @JsonAlias("pict_url")
    private String pictUrl;
    @JsonAlias("small_images")
    private DtkGetTbServiceImagesResponse smallImages;
    @JsonAlias("reserve_price")
    private String reservePrice;
    @JsonAlias("zk_final_price")
    private String zkFinalPrice;
    @JsonAlias("user_type")
    private Integer userType;
    @JsonAlias("seller_id")
    private Long sellerId;
    @JsonAlias("coupon_total_count")
    private Integer couponTotalCount;
    @JsonAlias("coupon_remain_count")
    private Integer couponRemainCount;
    @JsonAlias("coupon_info")
    private String couponInfo;
    @JsonAlias("shop_title")
    private String shopTitle;
    @JsonAlias("shop_dsr")
    private Integer shopDsr;
    @JsonAlias("level_one_category_name")
    private String levelOneCategoryName;
    @JsonAlias("level_one_category_id")
    private Integer levelOneCategoryId;
    @JsonAlias("category_name")
    private String categoryName;
    @JsonAlias("category_id")
    private Integer categoryId;
    @JsonAlias("short_title")
    private String shortTitle;
    @JsonAlias("white_image")
    private String whiteImage;
    @JsonAlias("coupon_start_fee")
    private String couponStartFee;
    @JsonAlias("coupon_amount")
    private String couponAmount;
    @JsonAlias("item_description")
    private String itemDescription;
    @JsonAlias("item_id")
//    private Long itemId;
    private String itemId;
    @JsonAlias("commission_rate")
    private String commissionRate;
    @JsonAlias("ysyl_tlj_face")
    private BigDecimal ysylTljFace;
    @JsonAlias("presale_deposit")
    private BigDecimal presaleDeposit;
    @JsonAlias("presale_discount_fee_text")
    private String presaleDiscountFeeText;
    @JsonAlias("activity_id")
    private String activityId;
    @JsonAlias("cpa_reward_amount")
    private BigDecimal cpaRewardAmount;
}
