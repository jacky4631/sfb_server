package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkSearchSuggestionResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 联想词请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:09
 */
@Getter
@Setter
public class DtkSearchSuggestionRequest implements DtkApiRequest<DtkApiResponse<List<DtkSearchSuggestionResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.2")
    private String version = "v1.0.2";
    @RequiredCheck
    @ApiModelProperty(value = "关键词", required = true)
    private String keyWords;
    @RequiredCheck
    @ApiModelProperty(value = "当前搜索API类型：1.大淘客搜索 2.联盟搜索 3.超级搜索", required = true)
    private Integer type;
    @ApiModelProperty("联想词请求path")
    private final String requestPath = "/goods/search-suggestion";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkSearchSuggestionResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkSearchSuggestionResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
