package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkJdCommodityDiscountPriceResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 京东商品历史券后价请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkJdCommodityDiscountPriceRequest implements DtkApiRequest<DtkApiResponse<DtkJdCommodityDiscountPriceResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty("商品id")
    private Integer skuId;
    @ApiModelProperty("查询时间类型：默认30天，可以1-近7天，2-近30天，3-近60天")
    private String offsetDays;
    @ApiModelProperty("京东商品历史券后价请求path")
    private final String requestPath = "/dels/jd/stats/goods/historyPriceRecords";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkJdCommodityDiscountPriceResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkJdCommodityDiscountPriceResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
