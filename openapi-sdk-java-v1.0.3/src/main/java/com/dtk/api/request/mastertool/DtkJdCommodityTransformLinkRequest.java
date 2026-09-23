package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkJdCommodityTransformLinkResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 京东商品转链请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 */
@Getter
@Setter
public class DtkJdCommodityTransformLinkRequest implements DtkApiRequest<DtkApiResponse<DtkJdCommodityTransformLinkResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "目标推客的联盟ID")
    private String unionId;
    @RequiredCheck
    @ApiModelProperty(value = "推广物料url，例如活动链接、商品链接等；不支持仅传入skuid")
    private String materialId;
    @ApiModelProperty(value = "新增推广位id （若无subUnionId权限，可入参该参数用来确定不同用户下单情况）")
    private Long positionId;
    @ApiModelProperty(value = "联盟子推客身份标识（不能传入接口调用者自己的pid）")
    private String childPid;
    @ApiModelProperty(value = "子渠道标识，您可自定义传入字母、数字或下划线，最多支持80个字符，该参数会在订单行查询接口中展示，需要有权限才可使用")
    private String subUnionId;
    @ApiModelProperty(value = "优惠券领取链接，在使用优惠券、商品二合一功能时入参，且materialId须为商品详情页链接（5.27更新：若不填则会自动匹配上全额最大的优惠券进行转链）")
    private String couponUrl;
    @ApiModelProperty(value = "转链类型，默认短链，短链有效期60天1：长链2：短链 3：长链+短链")
    private Integer chainType;
    @ApiModelProperty(value = "礼金批次号")
    private String giftCouponKey;
    @ApiModelProperty("京东商品转链请求path")
    private final String requestPath = "/dels/jd/kit/promotion-union-convert";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkJdCommodityTransformLinkResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkJdCommodityTransformLinkResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
