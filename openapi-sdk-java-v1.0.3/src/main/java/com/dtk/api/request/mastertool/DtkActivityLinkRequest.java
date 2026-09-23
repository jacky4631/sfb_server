package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkActivityLinkResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 官方活动会场转链请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:04
 */
@Getter
@Setter
public class DtkActivityLinkRequest implements DtkApiRequest<DtkApiResponse<DtkActivityLinkResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "联盟官方活动ID，从联盟官方活动页获取或从大淘客官方活动推广接口获取", required = true)
    private String promotionSceneId;
    @ApiModelProperty(value = "推广pid，默认为在”我的应用“添加的pid")
    private String pid;
    @ApiModelProperty(value = "渠道id将会和传入的pid进行验证，验证通过将正常转链，请确认填入的渠道id是正确的")
    private String relationId;
    @ApiModelProperty(value = "自定义输入串，英文和数字组成，长度不能大于12个字符，区分不同的推广渠道")
    private String unionId;
    @ApiModelProperty("官方活动会场转链请求path")
    private final String requestPath = "/tb-service/activity-link";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkActivityLinkResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkActivityLinkResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
