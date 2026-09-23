package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkListSimilerGoodsByOpenResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 猜你喜欢请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:21
 */
@Getter
@Setter
public class DtkListSimilerGoodsByOpenRequest implements DtkApiRequest<DtkApiResponse<List<DtkListSimilerGoodsByOpenResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.2")
    private String version = "v1.2.2";
    @RequiredCheck
    @ApiModelProperty(value = "大淘客的商品id", required = true)
    private Integer id;
    @ApiModelProperty(value = "每页条数，默认10，最大值100")
    private Integer size;
    @ApiModelProperty("猜你喜欢请求path")
    private final String requestPath = "/goods/list-similer-goods-by-open";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkListSimilerGoodsByOpenResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkListSimilerGoodsByOpenResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
