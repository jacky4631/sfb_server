package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkSuperDiscountGoodsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 折上折请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:12
 */
@Getter
@Setter
public class DtkSuperDiscountGoodsRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkSuperDiscountGoodsResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”")
    private String cids;
    @RequiredCheck
    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty("折上折请求path")
    private final String requestPath = "/goods/super-discount-goods";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkSuperDiscountGoodsResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkSuperDiscountGoodsResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
