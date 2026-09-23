package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkHistoricalNewLowCommodityResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 历史新低商品合集请求实体参数
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkHistoricalNewLowCommodityRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkHistoricalNewLowCommodityResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty("大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”。")
    private String cids;
    @RequiredCheck
    @ApiModelProperty("排序方式")
    private String sort;
    @ApiModelProperty("爆历史新低商品合集请求path")
    private final String requestPath = "/goods/get-history-low-price-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkHistoricalNewLowCommodityResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkHistoricalNewLowCommodityResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
