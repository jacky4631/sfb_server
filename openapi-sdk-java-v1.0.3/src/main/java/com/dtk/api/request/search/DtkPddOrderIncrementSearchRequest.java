package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.search.DtkPddOrderIncrementSearchResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Description 拼多多订单增量查询-请求实体
 * @Author FeiGuo
 * @CreateTime 2022-02-15 09:22:13
 * @Version 1.0
 */
@Getter
@Setter
public class DtkPddOrderIncrementSearchRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkPddOrderIncrementSearchResponse>>> {

    /**
     * 默认版本号
     */
    private String version = "v1.0.0";

    /**
     * 订单类型：1-推广订单；2-直播间订单
     */
    private Integer queryOrderType;

    /**
     * 查询开始时间，和开始时间相差不能超过24小时。
     */
    private String startUpdateTime;

    /**
     * 查询结束时间，和开始时间相差不能超过24小时。
     */
    private String endUpdateTime;

    /**
     * 拼多多联盟pid，多个使用英文逗号分隔符
     */
    private String pids;

    /**
     * 超级搜索请求path
     */
    private final String requestPath = "/dels/pdd/order/incrementSearch";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return "v1.0.0";
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkPddOrderIncrementSearchResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkPddOrderIncrementSearchResponse>>>() {};
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }

    @Override
    public void setVersion(String s) {

    }
}
