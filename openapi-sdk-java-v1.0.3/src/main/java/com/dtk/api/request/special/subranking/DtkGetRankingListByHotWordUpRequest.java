package com.dtk.api.request.special.subranking;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.subranking.DtkGetRankingListByHotWordUpResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

/**
 * 各大榜单5(热词飙升榜)请求参数实体
 *
 * @author baige
 * @date 2020/11/25 17:13
 */
@Getter
@Setter
public class DtkGetRankingListByHotWordUpRequest extends DtkGetRankingListBaseRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetRankingListByHotWordUpResponse>>> {
    @ApiModelProperty("各大榜单5请求path")
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
    public TypeReference<DtkApiResponse<List<DtkGetRankingListByHotWordUpResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetRankingListByHotWordUpResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
