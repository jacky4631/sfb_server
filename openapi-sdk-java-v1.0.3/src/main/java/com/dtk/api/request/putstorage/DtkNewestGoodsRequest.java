package com.dtk.api.request.putstorage;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.putstorage.DtkNewestGoodsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * 商品更新请求参数实体
 *
 * @author 1
 * @date 2020/11/10 16:21
 */
@Getter
@Setter
public class DtkNewestGoodsRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkNewestGoodsResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.0")
    private String version = "v1.2.0";
    @ApiModelProperty(value = "商品上架开始时间，请求格式：yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @ApiModelProperty(value = "商品上架结束时间，请求格式：yyyy-MM-dd HH:mm:ss")
    private String endTime;
    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”。当一级类目id和二级类目id同时传入时，会自动忽略二级类目id，1 -女装，2 -母婴，3 -美妆，4 -居家日用，5 -鞋品，6 -美食，7 -文娱车品，8 -数码家电，9 -男装，10 -内衣，11 -箱包，12 -配饰，13 -户外运动，14 -家装家纺")
    private String cids;
    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer subcid;
    @ApiModelProperty(value = "1-聚划算商品，0-所有商品，不填默认为0")
    private Integer juHuaSuan;
    @ApiModelProperty(value = "1-淘抢购商品，0-所有商品，不填默认为0")
    private Integer taoQiangGou;
    @ApiModelProperty(value = "1-天猫商品，0-非天猫商品，不填默认全部商品")
    private Integer tmall;
    @ApiModelProperty(value = "1-天猫超市商品，0-所有商品，不填默认为0")
    private Integer tchaoshi;
    @ApiModelProperty(value = "1-金牌卖家，0-所有商品，不填默认为0")
    private Integer goldSeller;
    @ApiModelProperty(value = "1-海淘，0-所有商品，不填默认为0")
    private Integer haitao;
    @ApiModelProperty(value = "1-品牌，0-所有商品，不填默认为0")
    private Integer brand;
    @ApiModelProperty(value = "品牌id，当brand传入0时，再传入brandIds将获取不到结果。品牌id可以传多个，以英文逗号隔开，如：”345,321,323”")
    private String brandIds;
    @ApiModelProperty(value = "1-活动预售商品，0-所有商品，不填默认为0")
    private Integer preSale;
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
    @ApiModelProperty(value = "排序字段，默认为0，0-综合排序，1-商品上架时间从新到旧，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty(value = "商品是否有券（0 - 全部商品，1 - 有券商品，-1 - 无券商品，默认为1）")
    private Integer hasCoupon;
    @ApiModelProperty("商品更新请求path")
    private final String requestPath = "/goods/get-newest-goods";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkNewestGoodsResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkNewestGoodsResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
