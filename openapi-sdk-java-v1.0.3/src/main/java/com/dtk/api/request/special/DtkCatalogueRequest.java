package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkCatalogueResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 精选专题请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:19
 */
@Getter
@Setter
public class DtkCatalogueRequest implements DtkApiRequest<DtkApiResponse<List<DtkCatalogueResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.1.0")
    private String version = "v1.1.0";
    @ApiModelProperty("精选专题请求path")
    private final String requestPath = "/goods/topic/catalogue";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkCatalogueResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkCatalogueResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
