package com.dtk.api.request.putstorage;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.putstorage.DtkPddGoodsDetailsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 拼多多商品详情请求参数实体
 *
 * @author 1
 * @date 2021/08/24 14:12
 */
@Getter
@Setter
public class DtkPddGoodsDetailsRequest implements DtkApiRequest<DtkApiResponse<DtkPddGoodsDetailsResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "商品goodsSign")
    private String goodsSign;
    @ApiModelProperty(value = "搜索id，建议填写，可提高收益")
    private Integer searchId;
    @ApiModelProperty(value = "商品主图类型：1-场景图，2-白底图，默认为0")
    private Integer goodsImgType;
    @ApiModelProperty("拼多多商品详情请求请求path")
    private Integer goodsId;
    @ApiModelProperty("商品ID（11/29新增字段）")
    private final String requestPath = "/dels/pdd/goods/detail";

    @Override
    public TypeReference<DtkApiResponse<DtkPddGoodsDetailsResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPddGoodsDetailsResponse>>() {
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
