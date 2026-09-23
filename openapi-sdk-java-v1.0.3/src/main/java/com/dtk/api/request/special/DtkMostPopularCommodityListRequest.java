package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkMostPopularCommodityListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 爆品预告商品合集请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkMostPopularCommodityListRequest implements DtkApiRequest<DtkApiResponse<List<DtkMostPopularCommodityListResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty("时间段1、昨天0点，2、昨天10点，3、今天0点，4、今天10点（默认），5、明天0点，6、明天10点")
    private Integer type;
    @ApiModelProperty("爆品预告商品合集请求path")
    private final String requestPath = "/goods/get-hot-advance";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkMostPopularCommodityListResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkMostPopularCommodityListResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
