package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkPriceTrendResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 淘宝商品历史券后价请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:14
 */
@Getter
@Setter
public class DtkPriceTrendRequest implements DtkApiRequest<DtkApiResponse<DtkPriceTrendResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "在大淘客的在线商品id（已下架的商品id不支持）")
    private String id;
    @ApiModelProperty(value = "淘宝商品id")
    private String goodsId;
    @ApiModelProperty("9.9包邮精选请求path")
    private final String requestPath = "/goods/price-trend";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPriceTrendResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPriceTrendResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
