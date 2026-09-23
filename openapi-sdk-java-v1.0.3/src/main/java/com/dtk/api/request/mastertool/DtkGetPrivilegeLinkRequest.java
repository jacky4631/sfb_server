package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkGetPrivilegeLinkResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 高效转链请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:07
 */
@Getter
@Setter
public class DtkGetPrivilegeLinkRequest implements DtkApiRequest<DtkApiResponse<DtkGetPrivilegeLinkResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.3.0")
    private String version = "v1.3.0";
    @RequiredCheck
    @ApiModelProperty(value = "淘宝商品id", required = true)
    private String goodsId;
    @ApiModelProperty(value = "商品的优惠券ID，一个商品在联盟可能有多个优惠券，可通过填写该参数的方式选择使用的优惠券，请确认优惠券ID正确，否则无法正常跳转")
    private String couponId;
    @ApiModelProperty(value = "推广位ID，用户可自由填写当前大淘客账号下已授权淘宝账号的任一pid，若未填写，则默认使用创建应用时绑定的pid")
    private String pid;
    @ApiModelProperty(value = "渠道id将会和传入的pid进行验证，验证通过将正常转链，请确认填入的渠道id是正确的，channelId对应联盟的relationId")
    private String channelId;
    @ApiModelProperty(value = "付定返红包，0.不使用付定返红包，1.参与付定返红包")
    private Integer rebateType;
    @ApiModelProperty(value = "会员运营id")
    private String specialId;
    @ApiModelProperty(value = "淘宝客外部用户标记，如自身系统账户ID；微信ID等")
    private String externalId;
    @ApiModelProperty(value = "团长与下游渠道合作的特殊标识，用于统计渠道推广效果")
    private String xid;
    @ApiModelProperty(value = "淘口令左边自定义符号,默认￥")
    private String leftSymbol;
    @ApiModelProperty(value = "淘口令右边自定义符号,默认￥ ")
    private String rightSymbol;
    @ApiModelProperty(value = "是否走高佣计划")
    private String highCommission;
    @ApiModelProperty("高效转链请求path")
    private final String requestPath = "/tb-service/get-privilege-link";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkGetPrivilegeLinkResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkGetPrivilegeLinkResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
