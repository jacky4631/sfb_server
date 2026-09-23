package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkSubdivisionCategoryListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 细分类目合集请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkSubdivisionCategoryListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkSubdivisionCategoryListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty("大淘客一级分类")
    private Integer cid;
    @ApiModelProperty("细分类目合集请求path")
    private final String requestPath = "/subdivision/get-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkSubdivisionCategoryListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkSubdivisionCategoryListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
