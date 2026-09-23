package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkTbkScRelationRefundResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Description 淘宝维权订单查询——请求实体
 * @Author FeiGuo
 * @CreateTime 2022-03-31 14:17:42
 * @Version 1.0
 */
@Getter
@Setter
public class DtkTbkScRelationRefundRequest implements DtkApiRequest<DtkApiResponse<DtkTbkScRelationRefundResponse>> {

    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";

    @ApiModelProperty("请求路径地址")
    private final String requestPath = "/tb-service/get-refund-order-list";

    @ApiModelProperty("TOP分配给应用的AppKey,必须参数")
    private String appKey;

    @ApiModelProperty("授权账号ID")
    private Integer authId;

    @ApiModelProperty("页大小，默认10，非必须")
    private Integer pageSize = 10;

    @ApiModelProperty("第几页，默认1，1~100.非必须")
    private Integer pageNo = 1;

    @ApiModelProperty("1-维权发起时间，2-订单结算时间（正向订单），3-维权完成时间，4-订单创建时间，5-订单更新时间")
    private Integer searchType;

    @ApiModelProperty("1 表示2方，2表示3方，0表示不限")
    private Integer refundType;

    @ApiModelProperty("1代表渠道关系id，2代表会员关系id")
    private Integer bizType;

    @ApiModelProperty("查询开始时间")
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
    public TypeReference<DtkApiResponse<DtkTbkScRelationRefundResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkTbkScRelationRefundResponse>>() {};
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
