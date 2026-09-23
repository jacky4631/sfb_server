package com.dtk.api.request.putstorage;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.putstorage.DtkGoodsListItemResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品列表请求参数实体
 *
 * @author 1
 * @date 2020/11/9 17:00
 */
@Getter
@Setter
public class DtkGoodsListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkGoodsListItemResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.4")
    private String version = "v1.2.4";
    @ApiModelProperty(value = "排序方式：默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高，7-券金额从高到底")
    private String sort;
    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”。当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String cids;
    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer subcid;
    @ApiModelProperty(value = "商品卖点：1-拍多件活动，2-多买多送，3-限量抢购，4-额外满减，6-买商品礼赠")
    private Integer specialId;
    @ApiModelProperty(value = "1-聚划算商品，0-所有商品，不填默认为0")
    private Integer juHuaSuan;
    @ApiModelProperty(value = "1-淘抢购商品，0-所有商品，不填默认为0")
    private Integer taoQiangGou;
    @ApiModelProperty(value = "1-天猫商品， 0-非天猫商品，不填默认所有商品")
    private Integer tmall;
    @ApiModelProperty(value = "1-天猫超市商品， 0-所有商品，不填默认为0")
    private Integer tchaoshi;
    @ApiModelProperty(value = "1-金牌卖家商品，0-所有商品，不填默认为0")
    private Integer goldSeller;
    @ApiModelProperty(value = "1-海淘商品， 0-所有商品，不填默认为0")
    private Integer haitao;
    @ApiModelProperty(value = "1-预告商品，0-所有商品，不填默认为0")
    private Integer pre;
    @ApiModelProperty(value = "1-活动预售商品，0-所有商品，不填默认为0")
    private Integer preSale;
    @ApiModelProperty(value = "1-品牌商品，0-所有商品，不填默认为0")
    private Integer brand;
    @ApiModelProperty(value = "当brand传入0时，再传入brandIds可能无法获取结果。品牌id可以传多个，以英文逗号隔开，如：”345,321,323”")
    private String brandIds;
    @ApiModelProperty(value = "价格（券后价）下限")
    private BigDecimal priceLowerLimit;
    @ApiModelProperty(value = "价格（券后价）上限")
    private BigDecimal priceUpperLimit;
    @ApiModelProperty(value = "最低优惠券面额")
    private BigDecimal couponPriceLowerLimit;
    @ApiModelProperty(value = "最低佣金比率")
    private BigDecimal commissionRateLowerLimit;
    @ApiModelProperty(value = "最低月销量")
    private BigDecimal monthSalesLowerLimit;
    @ApiModelProperty(value = "偏远地区包邮，1-是，0-非偏远地区，不填默认所有商品")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty(value = "定向佣金类型，3查询定向佣金商品，否则查询全部商品（12.22新增字段）")
    private Integer directCommissionType;
    @ApiModelProperty(value = "是否为精选商品，默认全部，1-精选商品（3.19新增字段）")
    private Integer choice;
    @ApiModelProperty(value = "1-官方旗舰店商品，0-不限是否是旗舰店，不填默认为0")
    private Integer flagShipStore;
    @ApiModelProperty(value = "1-30天新品，0-不限，不填默认为0（新品与最低价不能同时选，否则无商")
    private Integer isNew;
    @ApiModelProperty(value = "1-30天最低价，0-不限，不填默认为0（新品与最低价不能同时选，否则无商品）")
    private BigDecimal lowestPrice;
    @ApiModelProperty(value = "活动id，多个使用,分隔符。示例：1,2,3")
    private String activityId;
    @ApiModelProperty(value = "商品是否有券（0 - 全部商品，1 - 有券商品，-1 - 无券商品，默认为1）")
    private Integer hasCoupon;
    @ApiModelProperty(value = "是否已经验货，0-否；1-是")
    private Integer inspectedGoods;
    @ApiModelProperty(value = "最高优惠券面额")
    private BigDecimal maxCouponAmount;
    @ApiModelProperty(value = "最高佣金比例")
    private BigDecimal maxCommissionRate;
    @ApiModelProperty("商品列表请求path")
    private final String requestPath = "/goods/get-goods-list";

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkGoodsListItemResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkGoodsListItemResponse>>>() {
        };
    }

    @Override
    public Map<String, String> getTextParams() throws
            IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }


    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
