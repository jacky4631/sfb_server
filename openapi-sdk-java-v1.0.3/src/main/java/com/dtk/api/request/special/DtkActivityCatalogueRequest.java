package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkActivityCatalogueResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 热门活动请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkActivityCatalogueRequest implements DtkApiRequest<DtkApiResponse<List<DtkActivityCatalogueResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.1.0")
    private String version = "v1.1.0";
    @ApiModelProperty("热门活动请求path")
    private final String requestPath = "/goods/activity/catalogue";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkActivityCatalogueResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkActivityCatalogueResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
