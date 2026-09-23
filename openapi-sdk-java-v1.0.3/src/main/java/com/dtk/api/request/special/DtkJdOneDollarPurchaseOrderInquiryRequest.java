package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkJdOneDollarPurchaseOrderInquiryResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 京东一元购订单查询请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:12
 */
@Getter
@Setter
public class DtkJdOneDollarPurchaseOrderInquiryRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkJdOneDollarPurchaseOrderInquiryResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "订单时间类型：(1：下单时间，2：完成时间（购买用户确认收货时间），3：更新时间")
    private String orderTimeType;
    @RequiredCheck
    @ApiModelProperty(value = "开始时间 格式yyyy-MM-dd HH:mm:ss，与endTime间隔不超过30天")
    private String startTime;
    @ApiModelProperty(value = "结束时间 格式yyyy-MM-dd HH:mm:ss，与startTime间隔不超过30天")
    @RequiredCheck
    private String endTime;
    @ApiModelProperty(value = "自定义标识，用于区分下游推广渠道（如果没有做代理返利模式，可不传）")
    private String code;
    @ApiModelProperty("京东一元购请求path")
    private final String requestPath = "/dels/jd/order/outer/get-order-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkJdOneDollarPurchaseOrderInquiryResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkJdOneDollarPurchaseOrderInquiryResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
