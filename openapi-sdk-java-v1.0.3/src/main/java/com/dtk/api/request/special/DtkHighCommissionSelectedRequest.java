package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkHighCommissionSelectedResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 高佣精选请求实体参数
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkHighCommissionSelectedRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkHighCommissionSelectedResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty("大淘客的一级分类id")
    private Integer cid;
    @ApiModelProperty("大淘客的一级分类id")
    private Integer sort;
    @ApiModelProperty("高佣精选请求path")
    private final String requestPath = "/goods/singlePage/list-height-commission";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkHighCommissionSelectedResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkHighCommissionSelectedResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
