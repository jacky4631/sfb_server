package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkPddTransformLinkResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 拼多多商品转链请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 */
@Getter
@Setter
public class DtkPddTransformLinkRequest implements DtkApiRequest<DtkApiResponse<DtkPddTransformLinkResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "拼多多推广位id(联盟通知，在2021/4/30日之前，pid、custom_parameters必须进行备案)")
    private String pid;
    @RequiredCheck
    @ApiModelProperty(value = "商品goodsSign")
    private String goodsSign;
    @ApiModelProperty(value = "自定义参数，为链接打上自定义标签")
    private String customParameters;
    @ApiModelProperty(value = "招商多多客ID")
    private String zsDuoId;
    @ApiModelProperty("拼多多商品转链请求path")
    private final String requestPath = "/dels/pdd/kit/goods-prom-generate";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPddTransformLinkResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPddTransformLinkResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
