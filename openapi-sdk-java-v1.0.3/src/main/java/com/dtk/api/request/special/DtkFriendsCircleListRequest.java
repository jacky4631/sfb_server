package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkFriendsCircleListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 朋友圈文案请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:19
 */
@Getter
@Setter
public class DtkFriendsCircleListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkFriendsCircleListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.3.0")
    private String version = "v1.3.0";
    @ApiModelProperty(value = "大淘客的一级分类id，如6、4")
    private String cid;
    @ApiModelProperty(value = "大淘客的二级类目id，通过超级分类API获取。仅允许传一个二级id，当一级类目id和二级类目id同时传入时，会自动忽略二级类目id")
    private String subcid;
    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty(value = "是否预告商品，1-预告商品，0-所有商品，不填默认为0")
    private Integer pre;
    @ApiModelProperty(value = "偏远地区包邮，1-是，0-非偏远地区，不填默认所有商品")
    private Integer freeshipRemoteDistrict;
    @ApiModelProperty(value = "大淘客id或淘宝id，查询单个商品是否有朋友圈文案，如果有，则返回商品信息及朋友圈文案，如果没有，显示10006错误")
    private String goodsId;
    @ApiModelProperty("朋友圈文案请求path")
    private final String requestPath = "/goods/friends-circle-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkFriendsCircleListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkFriendsCircleListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
