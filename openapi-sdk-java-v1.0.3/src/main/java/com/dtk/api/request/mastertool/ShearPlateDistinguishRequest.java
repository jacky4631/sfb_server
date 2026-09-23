package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.ShearPlateDistinguishResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 剪切板识别请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:04
 */
@Getter
@Setter
public class ShearPlateDistinguishRequest implements DtkApiRequest<DtkApiResponse<ShearPlateDistinguishResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "文本内容")
    private String content;
    @ApiModelProperty(value = "淘宝联盟pid")
    private String tbPid;
    @ApiModelProperty(value = "淘宝联盟渠道id")
    private String tbChannelId;
    @ApiModelProperty(value = "京东联盟unionId")
    private Long jdUnionId;
    @ApiModelProperty(value = "京东联盟pid")
    private Long jdPid;
    @ApiModelProperty(value = "拼多多联盟pid")
    private String pddPid;
    @ApiModelProperty(value = "自定义参数")
    private String customerParameters;
    @ApiModelProperty("剪切板识别请求path")
    private final String requestPath = "/dels/kit/contentParse";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<ShearPlateDistinguishResponse>> responseType() {
        return new TypeReference<DtkApiResponse<ShearPlateDistinguishResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
