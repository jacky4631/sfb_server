package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkJdLinkAnalysisResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 京东链接解析请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 */
@Getter
@Setter
public class DtkJdLinkAnalysisRequest implements DtkApiRequest<DtkApiResponse<DtkJdLinkAnalysisResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "京东链接地址，内容URLEncode后使用")
    private String url;
    @ApiModelProperty("京东链接解析请求path")
    private final String requestPath = "/dels/jd/kit/parseUrl";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkJdLinkAnalysisResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkJdLinkAnalysisResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
