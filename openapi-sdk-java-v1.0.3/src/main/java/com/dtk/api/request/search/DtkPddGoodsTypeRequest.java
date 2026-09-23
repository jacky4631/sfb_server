package com.dtk.api.request.search;


import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkPddGoodsTypeResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 拼多多商品类目请求参数实体
 *
 * @author QC
 * @date 2021-9-24
 */
@Getter
@Setter
public class DtkPddGoodsTypeRequest implements DtkApiRequest<DtkApiResponse<List<DtkPddGoodsTypeResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty(value = "值=0时为顶点cat_id,通过树顶级节点获取cat树")
    private String parentId;
    @ApiModelProperty("拼多多商品类目请求path")
    private final String requestPath = "/dels/pdd/category/search";


    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkPddGoodsTypeResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkPddGoodsTypeResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
