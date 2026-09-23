package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkCzmfTransformLinkResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 超值买返商品转链请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 *
 * 121
 */
@Getter
@Setter
public class DtkCzmfTransformLinkRequest implements DtkApiRequest<DtkApiResponse<DtkCzmfTransformLinkResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "商品id")
    private String goodsId;
    @RequiredCheck
    @ApiModelProperty(value = "商品标题")
    private String title;
    @ApiModelProperty(value = "渠道id")
    private String relationId;
    @ApiModelProperty(value = "优惠券id")
    private String couponId;
    @ApiModelProperty(value = "淘宝联盟pid")
    private String  pid;
    @ApiModelProperty("超值买返商品转链请求path")
    private final String requestPath = "/dels/taobao/kit/turnLink/czmf";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkCzmfTransformLinkResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkCzmfTransformLinkResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
