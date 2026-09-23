package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkShopConvertResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 店铺转链请求参数实体
 *
 * @author baige
 * @date 2020/11/30 11:00
 */
@Getter
@Setter
public class DtkShopConvertRequest implements DtkApiRequest<DtkApiResponse<DtkShopConvertResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty(value = "店铺id")
    private String sellerId;
    @ApiModelProperty(value = "推广位id")
    private String pid;
    @ApiModelProperty(value = "渠道id")
    private String relationId;
    @ApiModelProperty(value = "店铺名称，用于返回淘口令")
    private String shopName;
    @ApiModelProperty("店铺转链请求path")
    private final String requestPath = "/dels/shop/convert";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.getVersion();
    }

    @Override
    public TypeReference<DtkApiResponse<DtkShopConvertResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkShopConvertResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }

}
