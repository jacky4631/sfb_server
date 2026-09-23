package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkOpGoodsListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 9.9包邮精选请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:16
 */
@Getter
@Setter
public class DtkOpGoodsListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkOpGoodsListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v2.0.0")
    private String version = "v2.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "9.9精选的类目id，分类id请求详情：-1-精选，1 -5.9元区，2 -9.9元区，3 -19.9元区", required = true)
    private String nineCid;
    @ApiModelProperty("9.9包邮精选请求path")
    private final String requestPath = "/goods/nine/op-goods-list";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkOpGoodsListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkOpGoodsListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
