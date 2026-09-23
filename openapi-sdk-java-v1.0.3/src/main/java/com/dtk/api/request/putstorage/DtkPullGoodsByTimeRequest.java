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

import java.util.TreeMap;

/**
 * 定时拉取请求参数实体
 *
 * @author 1
 * @date 2020/11/10 16:20
 */
@Getter
@Setter
public class DtkPullGoodsByTimeRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkGoodsListItemResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.3")
    private String version = "v1.2.3";
    @ApiModelProperty(value = "大淘客的一级分类id。当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String cid;
    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private Integer subcid;
    @ApiModelProperty(value = "是否预告商品，1-预告商品，0-所有商品，不填默认为0")
    private Integer pre;
    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从新到旧，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty(value = "开始时间，格式为yy-mm-dd hh:mm:ss，商品下架的时间大于等于开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间，格式为yy-mm-dd hh:mm:ss，默认为请求的时间，商品下架的时间小于等于结束时间")
    private String endTime;
    @ApiModelProperty(value = "偏远地区包邮，1-是，0-非偏远地区，不填默认所有商品")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty(value = "是否为精选商品，默认全部商品，1-精选商品（3.19新增字段）")
    private Integer choice;
    @ApiModelProperty(value = "商品是否有券（0 - 全部商品，1 - 有券商品，-1 - 无券商品，默认为1）")
    private Integer hasCoupon;
    @ApiModelProperty("定时拉取请求path")
    private final String requestPath = "/goods/pull-goods-by-time";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

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
    public String requestUrl() {
        return this.requestPath;
    }
}
