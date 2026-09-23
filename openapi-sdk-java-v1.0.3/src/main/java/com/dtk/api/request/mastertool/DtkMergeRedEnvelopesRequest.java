package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkMergeRedEnvelopesResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 三合一红包接口请求参数实体
 *
 * @author 1
 * @date 2020/11/10 17:56
 */
@Getter
@Setter
public class DtkMergeRedEnvelopesRequest implements DtkApiRequest<DtkApiResponse<DtkMergeRedEnvelopesResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "类型：1-淘宝红包，2京东红包，3-拼多多红包", required = true)
    private Integer merchantType;
    @ApiModelProperty(value = "推广位ID（大淘客账号下已授权淘宝账号的任一pid，若未填写，则默认使用创建应用时绑定的pid；其中京东pid为联盟子推客身份标识（不能传入接口调用者自己的pid）")
    private String pid;
    @ApiModelProperty(value = "选择京东红包时需要填入京东联盟ID（在京东联盟后台个人中心）。其他类型不用传", required = true)
    private String unionId;
    @ApiModelProperty("三合一红包请求path")
    private final String requestPath = "/dels/merge-red-envelopes";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkMergeRedEnvelopesResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkMergeRedEnvelopesResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
