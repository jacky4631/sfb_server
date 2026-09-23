package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkJdOrderQueryResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 京东订单查询请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 */
@Getter
@Setter
public class DtkJdOrderQueryRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<List<DtkJdOrderQueryResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "页码（默认为1）")
    private Integer pageNo;
    @ApiModelProperty(value = "订单时间查询类型(1：下单时间，2：完成时间（购买用户确认收货时间），3：更新时间")
    private String type;
    @ApiModelProperty(value = "子推客unionID，传入该值可查询子推客的订单，注意不可和key同时传入。（需联系运营开通PID权限才能拿到数据）")
    private String childUnionId;
    @RequiredCheck
    @ApiModelProperty(value = "工具商传入推客的授权key，可帮助该推客查询订单，注意不可和childUnionid同时传入。（需联系运营开通工具商权限才能拿到数据，请在京东联盟->我的工具->我的API->领取授权KEY中获取key）")
    private String key;
    @RequiredCheck
    @ApiModelProperty(value = "开始时间 格式yyyy-MM-dd HH:mm:ss，与endTime间隔不超过1小时")
    private String startTime;
    @RequiredCheck
    @ApiModelProperty(value = "结束时间 格式yyyy-MM-dd HH:mm:ss，与startTime间隔不超过1小时")
    private String endTime;
    @ApiModelProperty(value = "支持出参数据筛选，逗号分隔，目前可用：goodsInfo（商品信息）,categoryInfo(类目信息）")
    private String fields;
    @ApiModelProperty("京东订单查询请求path")
    private final String requestPath = "/dels/jd/order/get-official-order-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<List<DtkJdOrderQueryResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<List<DtkJdOrderQueryResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
