package com.dtk.api.request.putstorage;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.putstorage.DtkJdCommodityDetailsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 京东商品详情
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkJdCommodityDetailsRequest implements DtkApiRequest<DtkApiResponse<List<DtkJdCommodityDetailsResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty("商品skuId，多个使用逗号分隔，最多支持10个skuId同时查询（需使用半角状态下的逗号）")
    private String skuIds;
    @ApiModelProperty("京东商品详情请求path")
    private final String requestPath = "/dels/jd/goods/get-details";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkJdCommodityDetailsResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkJdCommodityDetailsResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
