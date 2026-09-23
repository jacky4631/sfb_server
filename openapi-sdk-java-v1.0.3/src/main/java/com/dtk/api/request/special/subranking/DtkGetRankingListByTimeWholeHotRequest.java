package com.dtk.api.request.special.subranking;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.subranking.DtkGetRankingListByTimeWholeHotResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

/**
 * 各大榜单(1(实时榜)，2(全天榜)，3(热推榜))请求参数实体
 *
 * @author baige
 * @date 2020/11/25 16:36
 */
@Getter
@Setter
public class DtkGetRankingListByTimeWholeHotRequest extends DtkGetRankingListBaseRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetRankingListByTimeWholeHotResponse>>> {
    @ApiModelProperty("各大榜单(1(实时榜)，2(全天榜)，3(热推榜))path")
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
    public TypeReference<DtkApiResponse<List<DtkGetRankingListByTimeWholeHotResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetRankingListByTimeWholeHotResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
