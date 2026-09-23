package com.dtk.api.request.putstorage;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.putstorage.DtkStaleGoodsByTimeResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 失效商品请求参数实体
 *
 * @author 1
 * @date 2020/11/10 16:20
 */
@Getter
@Setter
public class DtkStaleGoodsByTimeRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkStaleGoodsByTimeResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.1")
    private String version = "v1.0.1";
    @ApiModelProperty(value = "开始时间，默认为yyyy-mm-dd hh:mm:ss，商品下架的时间大于等于开始时间，开始时间需要在当日")
    private String startTime;
    @ApiModelProperty(value = "结束时间，默认为请求的时间，商品下架的时间小于等于结束时间，结束时间需要在当日")
    private String endTime;
    @ApiModelProperty("失效商品请求path")
    private final String requestPath = "/goods/get-stale-goods-by-time";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkStaleGoodsByTimeResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkStaleGoodsByTimeResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
