package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkCreatTaokoulingResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 淘口令生成请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:05
 */
@Getter
@Setter
public class DtkCreatTaokoulingRequest implements DtkApiRequest<DtkApiResponse<DtkCreatTaokoulingResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @RequiredCheck
    @ApiModelProperty(value = "口令弹框内容，长度大于5个字符", required = true)
    private String text;
    @RequiredCheck
    @ApiModelProperty(value = "口令跳转目标页，如：https://uland.taobao" +
            ".com/，必须以https开头，可以是二合一链接、长链接、短链接等各种淘宝高佣链接；支持渠道备案链接。该参数需要进行UrlEncode编码后传入", required = true)
    private String url;
    @ApiModelProperty(value = "口令弹框logoURL")
    private String logo;
    @ApiModelProperty(value = "生成口令的淘宝用户ID，非必传参数")
    private String userId;
    @ApiModelProperty("淘口令生成请求path")
    private final String requestPath = "/tb-service/creat-taokouling";

    @Override
    public TypeReference<DtkApiResponse<DtkCreatTaokoulingResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkCreatTaokoulingResponse>>() {
        };
    }

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }


    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
