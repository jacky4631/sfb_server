package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkGetSuperCategoryResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 超级分类请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:07
 */
@Getter
@Setter
public class DtkGetSuperCategoryRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetSuperCategoryResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.1.0")
    private String version = "v1.1.0";
    @ApiModelProperty("超级分类请求path")
    private final String requestPath = "/category/get-super-category";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkGetSuperCategoryResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetSuperCategoryResponse>>>() {
        };
    }


    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
