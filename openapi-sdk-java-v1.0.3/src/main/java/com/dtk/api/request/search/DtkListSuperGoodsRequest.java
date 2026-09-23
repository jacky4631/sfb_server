package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.search.DtkListSuperGoodsResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 超级搜索请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:09
 */
@Getter
@Setter
public class DtkListSuperGoodsRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkListSuperGoodsResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.3.0")
    private String version = "v1.3.0";
    @RequiredCheck
    @ApiModelProperty(value = "搜索类型：0-综合结果，1-大淘客商品，2-联盟商品", required = true)
    private Integer type;
    @RequiredCheck
    @ApiModelProperty(value = "关键词搜索", required = true)
    private String keyWords;
    @ApiModelProperty(value = "是否天猫商品：1-天猫商品，0-所有商品，不填默认为0")
    private Integer tmall;
    @ApiModelProperty(value = "是否海淘商品：1-海淘商品，0-所有商品，不填默认为0")
    private Integer haitao;
    @ApiModelProperty(value = "排序字段信息 销量（total_sales） 价格（price），排序_des（降序），排序_asc（升序），示例：升序查询销量total_sales_asc")
    private String sort;
    @ApiModelProperty(value = "会员运营id")
    private String specialId;
    @ApiModelProperty(value = "渠道id将会和传入的pid进行验证，验证通过将正常转链，请确认填入的渠道id是正确的，channelId对应联盟的relationId")
    private String channelId;
    @ApiModelProperty(value = "商品券后价下限(2021/1/15新增字段)")
    private String priceLowerLimit;
    @ApiModelProperty(value = "商品券后价上限(2021/1/15新增字段)")
    private String priceUpperLimit;
    @ApiModelProperty(value = "淘客佣金比率上限(2021/1/15新增字段)")
    private String endTkRate;
    @ApiModelProperty(value = "淘客佣金比率下限(2021/1/15新增字段)")
    private String startTkRate;
    @ApiModelProperty(value = "是否有券，1为有券，默认为全部(2021/1/15新增字段)")
    private String hasCoupon;
    @ApiModelProperty(value = "活动id，多个使用,分隔符。示例：1,2,3")
    private String activityId;
    @ApiModelProperty(value = "是否已经验货，0-否；1-是")
    private Integer inspectedGoods;
    @ApiModelProperty("超级搜索请求path")
    private final String requestPath = "/goods/list-super-goods";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public java.lang.String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkListSuperGoodsResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkListSuperGoodsResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
