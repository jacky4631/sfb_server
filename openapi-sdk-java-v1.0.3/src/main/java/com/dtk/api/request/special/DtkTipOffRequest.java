package com.dtk.api.request.special;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.special.DtkTipOffResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 线报
 *
 * @author 1
 * @date 2020/11/10 18:22
 */
@Getter
@Setter
public class DtkTipOffRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkTipOffResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty("线报类型：1-超值买返2-天猫超市3-整点抢购4-最新线报-所有数据(默认)5-最新线报-天猫6-最新线报-京东7-最新线报-拼多多8-最新线报-淘宝")
    private Integer topic;
    @ApiModelProperty("rush-整点抢购时的时间戳（秒），示例：1617026400")
    private Integer selectTime;
    @ApiModelProperty("线报请求path")
    private final String requestPath = "/dels/spider/list-tip-off";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkTipOffResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkTipOffResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
