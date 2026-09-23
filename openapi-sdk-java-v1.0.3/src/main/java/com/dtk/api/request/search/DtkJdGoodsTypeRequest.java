package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkJdGoodsTypeResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 京东商品类目请求参数实体
 *
 * @author QC
 * @date 2021-9-24
 */
@Getter
@Setter
public class DtkJdGoodsTypeRequest implements DtkApiRequest<DtkApiResponse<List<DtkJdGoodsTypeResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty(value = "值=0时为顶点cat_id,通过树顶级节点获取cat树")
    private String parentId;
    @ApiModelProperty(value = "类目级别")
    private String level;
    @ApiModelProperty("京东商品类目请求path")
    private final String requestPath = "/dels/jd/category/search";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkJdGoodsTypeResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkJdGoodsTypeResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
