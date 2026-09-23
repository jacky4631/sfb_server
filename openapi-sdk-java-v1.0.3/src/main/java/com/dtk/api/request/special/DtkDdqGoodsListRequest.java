package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkDdqGoodsListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 咚咚抢请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:18
 */
@Getter
@Setter
public class DtkDdqGoodsListRequest implements DtkApiRequest<DtkApiResponse<DtkDdqGoodsListResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.1")
    private String version = "v1.2.1";
    @ApiModelProperty(value = "默认为当前场次，场次时间输入方式：yyyy-mm-dd hh:mm:ss")
    private String roundTime;
    @ApiModelProperty("咚咚抢请求path")
    private final String requestPath = "/category/ddq-goods-list";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkDdqGoodsListResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkDdqGoodsListResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
