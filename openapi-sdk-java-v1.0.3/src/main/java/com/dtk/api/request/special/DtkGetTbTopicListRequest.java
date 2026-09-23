package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkGetTbTopicListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 官方活动(1元购)请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:20
 */
@Getter
@Setter
public class DtkGetTbTopicListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetTbTopicListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.0")
    private String version = "v1.2.0";
    @ApiModelProperty(value = "输出的端口类型：0-全部（默认），1-PC，2-无线")
    private Integer type;
    @ApiModelProperty(value = "阿里妈妈上申请的渠道id")
    private String channelId;
    @ApiModelProperty("官方活动(1元购)请求path")
    private final String requestPath = "/category/get-tb-topic-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkGetTbTopicListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetTbTopicListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
