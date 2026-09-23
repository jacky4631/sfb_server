package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkTbCashGiftResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 淘礼金创建请求参数实体
 *
 * @author 1
 * @date 2021/07/07 14:27
 */
@Getter
@Setter
public class DtkTbCashGiftRequest implements DtkApiRequest<DtkApiResponse<DtkTbCashGiftResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "1.0.0";
    @ApiModelProperty(value = "二合一链接，淘口令，或同时输入商品+优惠券链接")
    private String content;
    @RequiredCheck
    @ApiModelProperty(value = "请填写您申请的具有淘礼金应用权限的Appkey")
    private String alimamaAppKey;
    @RequiredCheck
    @ApiModelProperty(value = "请填写您申请的具有淘礼金应用权限的Appsecret")
    private String alimamaAppSecret;
    @RequiredCheck
    @ApiModelProperty(value = "宝贝商品id")
    private String itemId;
    @RequiredCheck
    @ApiModelProperty(value = "淘礼金名称，最大10个字符")
    private String name;
    @ApiModelProperty(value = "佣金计划类型1-定向（DX）；2-鹊桥（LINK_EVENT）；3-营销（MKT）")
    private Integer campaignType;
    @RequiredCheck
    @ApiModelProperty(value = "单个淘礼金面额，支持两位小数，单位元")
    private String perFace;
    @RequiredCheck
    @ApiModelProperty(value = "淘礼金总个数")
    private Integer totalNum;
    @RequiredCheck
    @ApiModelProperty(value = "单用户累计中奖次数上限，最小值为1")
    private Integer winNumLimit;
    @RequiredCheck
    @ApiModelProperty(value = "发放开始时间，格式为yyyy-MM-dd HH:mm:ss示例：发放开始时间 2018-09-01 00:00:00")
    private String sendStartTime;
    @RequiredCheck
    @ApiModelProperty(value = "发放截止时间，格式为yyyy-MM-dd HH:mm:ss发放截止时间，示例： 2018-09-01 00:00:00")
    private String sendEndTime;
    @ApiModelProperty(value = "结束日期的模式,1:相对时间，2:绝对时间使用结束日期")
    private String useEndTimeMode;
    @ApiModelProperty(value = "使用开始日期。相对时间，无需填写，以用户领取时间作为使用开始时间")
    private String useStartTime;
    @ApiModelProperty(value = "使用结束日期。如果是结束时间模式为相对时间，时间格式为1-7直接的整数")
    private String userEndTime;
    @ApiModelProperty(value = "联盟应用对应的pid")
    private String pid;

    @ApiModelProperty("淘礼金创建请求path")
    private final String requestPath = "/dels/taobao/kit/create-tlj";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkTbCashGiftResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkTbCashGiftResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
