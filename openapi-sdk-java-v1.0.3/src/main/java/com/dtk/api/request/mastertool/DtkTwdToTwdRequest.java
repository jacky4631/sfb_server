package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkTwdToTwdResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 淘口令转淘口令请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:05
 */
@Getter
@Setter
public class DtkTwdToTwdRequest implements DtkApiRequest<DtkApiResponse<DtkTwdToTwdResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "支持包含文本的淘口令，但最好是一个单独淘口令")
    private String content;
    @ApiModelProperty(value = "推广位ID，用户可自由填写当前大淘客账号下已授权淘宝账号的任一pid，若未填写，则默认使用创建应用时绑定的pid")
    private String pid;
    @ApiModelProperty(value = "渠道id将会和传入的pid进行验证，验证通过将正常转链，请确认填入的渠道id是正确的")
    private String channelId;
    @ApiModelProperty(value = "会员运营ID")
    private String special_id;
    @ApiModelProperty(value = "淘宝客外部用户标记，如自身系统账户ID；微信ID等")
    private String external_id;
    @ApiModelProperty("淘口令转淘口令请求path")
    private final String requestPath = "/tb-service/twd-to-twd";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkTwdToTwdResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkTwdToTwdResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
