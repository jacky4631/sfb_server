package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.mastertool.DtkGetOwnerGoodsItemResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 我发布的商品请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:08
 */
@Getter
@Setter
public class DtkGetOwnerGoodsRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkGetOwnerGoodsItemResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.1")
    private String version = "v1.0.1";
    @ApiModelProperty(value = "是否下架，默认为1,1-未下架商品，0-已下架商品")
    private Integer online;
    @ApiModelProperty(value = "商品提交开始时间，请求格式：yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @ApiModelProperty(value = "商品上架结束时间，请求格式：yyyy-MM-dd HH:mm:ss")
    private String endTime;
    @ApiModelProperty(value = "排序字段，默认为0，0-综合排序，1-商品上架时间从新到旧，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty("我发布的商品请求path")
    private final String requestPath = "/goods/get-owner-goods";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkGetOwnerGoodsItemResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkGetOwnerGoodsItemResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
