package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.putstorage.DtkBaseItemResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 专题商品请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:19
 */
@Getter
@Setter
public class DtkTopicGoodsListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkBaseItemResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.2")
    private String version = "v1.2.2";
    @RequiredCheck
    @ApiModelProperty(value = "专辑id，通过精选专辑API获取的活动id", required = true)
    private Integer topicId;
    @ApiModelProperty("专题商品请求path")
    private final String requestPath = "/goods/topic/goods-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkBaseItemResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkBaseItemResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
