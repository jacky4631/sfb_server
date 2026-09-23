package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkActivityGoodsListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 活动商品请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkActivityGoodsListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkActivityGoodsListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.3")
    private String version = "v1.2.2";
    @RequiredCheck
    @ApiModelProperty(value = "通过热门活动API获取的活动id", required = true)
    private Integer activityId;
    @ApiModelProperty(value = "大淘客一级分类ID：1 -女装，2 -母婴，3 -美妆，4 -居家日用，5 -鞋品，6 -美食，7 -文娱车品，8 -数码家电，9 -男装，10 -内衣，11 -箱包，12 -配饰，13 -户外运动，14 -家装家纺")
    private Integer cid;
    @ApiModelProperty(value = "大淘客二级分类ID：可通过超级分类接口获取二级分类id，当同时传入一级分类id和二级分类id时，以一级分类id为准")
    private Integer subcid;
    @ApiModelProperty(value = "偏远地区包邮：1.是，0.否")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty("活动商品请求path")
    private final String requestPath = "/goods/activity/goods-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkActivityGoodsListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkActivityGoodsListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
