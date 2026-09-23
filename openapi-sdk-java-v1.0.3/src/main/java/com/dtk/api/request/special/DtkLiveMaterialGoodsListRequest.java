package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkLiveMaterialGoodsListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 直播好货请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:14
 */
@Getter
@Setter
public class DtkLiveMaterialGoodsListRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkLiveMaterialGoodsListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty(value = "选择某一天的直播商品数据，默认返回全部参与过直播，且未下架的商品。时间格式：2020-09-16")
    private String date;
    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty("直播好货请求path")
    private final String requestPath = "/goods/liveMaterial-goods-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkLiveMaterialGoodsListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkLiveMaterialGoodsListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
