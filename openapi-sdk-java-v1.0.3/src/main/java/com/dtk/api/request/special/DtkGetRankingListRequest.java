package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.special.subranking.DtkGetRankingListBaseRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.special.DtkGetRankingListMergeResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.TreeMap;

/**
 * 各大榜单(包含所有榜单类型)请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:18
 * @see com.dtk.api.request.special.subranking 建议以此包里的其它以byxxxRequest结尾对象进行查询操作
 */
@Getter
@Setter
public class DtkGetRankingListRequest extends DtkGetRankingListBaseRequest implements DtkApiRequest<DtkApiResponse<List<DtkGetRankingListMergeResponse>>> {
    @ApiModelProperty("各大榜单(包含所有榜单类型)请求path")
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
    public TypeReference<DtkApiResponse<List<DtkGetRankingListMergeResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkGetRankingListMergeResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
