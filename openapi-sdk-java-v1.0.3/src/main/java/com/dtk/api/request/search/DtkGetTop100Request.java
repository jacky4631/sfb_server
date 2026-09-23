package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkGetTop100Response;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 热搜记录请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:10
 */
@Getter
@Setter
public class DtkGetTop100Request implements DtkApiRequest<DtkApiResponse<DtkGetTop100Response>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.1")
    private String version = "v1.0.1";
    @ApiModelProperty(value = "1：买家热搜榜（默认）、2：淘客热搜榜")
    private Integer type;
    @ApiModelProperty("热搜记录请求path")
    private final String requestPath = "/category/get-top100";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkGetTop100Response>> responseType() {
        return new TypeReference<DtkApiResponse<DtkGetTop100Response>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
