package com.dtk.api.request.special.subranking;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.subranking.DtkGetRankingListByHotSearchResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

/**
 * 各大榜单7(综合热搜榜)请求参数实体
 *
 * @author baige
 * @date 2020/11/25 17:13
 */
@Getter
@Setter
public class DtkGetRankingListByHotSearchRequest extends DtkGetRankingListBaseRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetRankingListByHotSearchResponse>>> {

    @ApiModelProperty("各大榜单7请求path")
    private final String requestPath = "/goods/get-ranking-list";
    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return super.getVersion();
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkGetRankingListByHotSearchResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetRankingListByHotSearchResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
