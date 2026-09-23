package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkGetOrderDetailsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 淘系订单查询接口请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:06
 */
@Getter
@Setter
public class DtkGetOrderDetailsRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkGetOrderDetailsResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "查询时间类型，1：按照订单淘客创建时间查询，2:按照订单淘客付款时间查询，3:按照订单淘客结算时间查询")
    private Integer queryType;
    @ApiModelProperty(value = "位点，除第一页之外，都需要传递；前端原样返回。")
    private String positionIndex;
    @ApiModelProperty(value = "页大小，默认20，1~100")
    private Integer pageSize;
    @ApiModelProperty(value = "推广者角色类型,2:二方，3:三方，不传，表示所有角色")
    private Integer memberType;
    @ApiModelProperty(value = "淘客订单状态，12-付款，13-关闭，14-确认收货，3-结算成功;不传，表示所有状态")
    private Integer tkStatus;
    @RequiredCheck
    @ApiModelProperty(value = "订单查询结束时间，订单开始时间至订单结束时间，中间时间段日常要求不超过3个小时，但如618、双11、年货节等大促期间预估时间段不可超过20" +
            "分钟，超过会提示错误，调用时请务必注意时间段的选择，以保证亲能正常调用！ 时间格式：YYYY-MM-DD HH:MM:SS", required = true)
    private String endTime;
    @RequiredCheck
    @ApiModelProperty(value = "订单查询开始时间。时间格式：YYYY-MM-DD HH:MM:SS", required = true)
    private String startTime;
    @ApiModelProperty(value = "跳转类型，当向前或者向后翻页必须提供,-1: 向前翻页,1：向后翻页")
    private Integer jumpType;
    @ApiModelProperty(value = "第几页，默认1，1~100")
    private Integer pageNo;
    @ApiModelProperty(value = "场景订单场景类型，1:常规订单，2:渠道订单，3:会员运营订单，默认为1")
    private Integer orderScene;
    @ApiModelProperty("淘系订单查询接口请求path")
    private final String requestPath = "/tb-service/get-order-details";


    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkGetOrderDetailsResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkGetOrderDetailsResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
