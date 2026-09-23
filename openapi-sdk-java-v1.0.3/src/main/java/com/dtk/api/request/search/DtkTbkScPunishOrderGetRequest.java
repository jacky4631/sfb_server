package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkTbkScPunishOrderGetResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Description 淘宝违规订单——查询实体
 * @Author FeiGuo
 * @CreateTime 2022-03-31 14:36:09
 * @Version 1.0
 */
@Getter
@Setter
public class DtkTbkScPunishOrderGetRequest implements DtkApiRequest<DtkApiResponse<DtkTbkScPunishOrderGetResponse>> {

    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";

    @ApiModelProperty("请求路径地址")
    private final String requestPath = "/tb-service/get-foul-order-list";

    @ApiModelProperty("子订单号")
    private String tbTradeId;

    @ApiModelProperty("第几页，默认1，1~100")
    private Integer pageNo;

    @ApiModelProperty("页大小，默认10，非必须")
    private Integer pageSize;

    @ApiModelProperty("查询时间跨度，不超过30天，单位是天")
    private Integer span;

    @ApiModelProperty("查询开始时间，以淘客订单创建时间开始")
    private String startTime;

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkTbkScPunishOrderGetResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkTbkScPunishOrderGetResponse>>() {};
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }
}
