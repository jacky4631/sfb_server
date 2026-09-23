package com.dtk.api.request.putstorage;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.putstorage.DtkGoodsDetailsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 单品详情请求参数实体
 *
 * @author 1
 * @date 2020/11/10 15:28
 */
@Getter
@Setter
public class DtkGoodsDetailsRequest implements DtkApiRequest<DtkApiResponse<DtkGoodsDetailsResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.2.3")
    private String version = "v1.2.3";
    @ApiModelProperty(value = "大淘客商品id，请求时id或goodsId必填其中一个，若均填写，将优先查找当前单品id")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id，id或goodsId必填其中一个，若均填写，将优先查找当前单品id")
    private String goodsId;
    @ApiModelProperty("单品详情请求path")
    private final String requestPath = "/goods/get-goods-details";

    @Override
    public TypeReference<DtkApiResponse<DtkGoodsDetailsResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkGoodsDetailsResponse>>() {
        };
    }

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
