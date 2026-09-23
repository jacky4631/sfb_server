package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkJdBatchTransformLinkResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 京东商品批量转链请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 */
@Getter
@Setter
public class DtkJdBatchTransformLinkRequest implements DtkApiRequest<DtkApiResponse<DtkJdBatchTransformLinkResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "目标推客的联盟ID")
    private String unionId;
    @ApiModelProperty(value = "新增推广位id （若无subUnionId权限，可入参该参数用来确定不同用户下单情况）")
    private Long positionId;
    @ApiModelProperty(value = "联盟子推客身份标识（不能传入接口调用者自己的pid）")
    private String childPid;
    @ApiModelProperty(value = "子渠道标识，您可自定义传入字母、数字或下划线，最多支持80个字符，该参数会在订单行查询接口中展示，需要有权限才可使用")
    private String subUnionId;
    @RequiredCheck
    @ApiModelProperty(value = "待转链京东商品物料地址(需要urlencode，优惠券无法进行转链，无法转链的地址会按照原数据返回)")
    private String content;
    @ApiModelProperty("京东商品批量转链请求path")
    private final String requestPath = "/dels/jd/kit/content/promotion-union-convert";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkJdBatchTransformLinkResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkJdBatchTransformLinkResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
