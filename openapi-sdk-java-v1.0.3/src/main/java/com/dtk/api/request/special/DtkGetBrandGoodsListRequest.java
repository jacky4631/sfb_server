package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkDiffPageResponse;
import com.dtk.api.response.special.DtkGetBrandGoodsListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 单个品牌详情请求参数实体
 *
 * @author baige
 * @date 2020/11/30 17:43
 */
@Getter
@Setter
public class DtkGetBrandGoodsListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkDiffPageResponse<DtkGetBrandGoodsListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "品牌id")
    private String brandId;
    @ApiModelProperty("单个品牌详情请求path")
    private final String requestPath = "/delanys/brand/get-goods-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkDiffPageResponse<DtkGetBrandGoodsListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkDiffPageResponse<DtkGetBrandGoodsListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }

}
