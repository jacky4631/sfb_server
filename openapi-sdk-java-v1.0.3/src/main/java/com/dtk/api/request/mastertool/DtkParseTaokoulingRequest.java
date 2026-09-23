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
 * 淘口令解析请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:06
 */
@Getter
@Setter
public class DtkParseTaokoulingRequest implements DtkApiRequest<DtkApiResponse<DtkParseContentResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "包含淘口令的文本。 若文本中有多个淘口令，仅解析第一个。（目前仅支持商品口令和二合一券口令）",
            required = true)
    private String content;
    @ApiModelProperty("淘口令解析请求path")
    private final String requestPath = "/tb-service/parse-taokouling";

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
