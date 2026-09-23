package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkParseContentResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 淘系万能解析请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:04
 */
@Getter
@Setter
public class DtkParseContentRequest implements DtkApiRequest<DtkApiResponse<DtkParseContentResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "包含淘口令、链接的文本。优先解析淘口令，再按序解析每个链接，直至解出有效信息。如果淘口令失效或者不支持的类型的情况，会按顺序解析链接。如果存在解析失败，请再试一次",
            required = true)
    private String content;
    @ApiModelProperty("淘系万能解析请求path")
    private final String requestPath = "/tb-service/parse-content";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkParseContentResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkParseContentResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
