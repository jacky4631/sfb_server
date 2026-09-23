package com.dtk.api.request.special.subranking;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.subranking.DtkGetRankingListByHotWordResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

/**
 * 各大榜单6(热词排行榜)请求参数实体
 *
 * @author baige
 * @date 2020/11/25 17:13
 */
@Getter
@Setter
public class DtkGetRankingListByHotWordRequest extends DtkGetRankingListBaseRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetRankingListByHotWordResponse>>> {
    @ApiModelProperty("各大榜单6请求path")
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
    public TypeReference<DtkApiResponse<List<DtkGetRankingListByHotWordResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetRankingListByHotWordResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
