package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkJdUnionQueryResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 京东联盟搜索请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:10
 */
@Getter
@Setter
public class DtkJdUnionQueryRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkJdUnionQueryResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty("一级类目id")
    private Long cid1;
    @ApiModelProperty("二级类目id")
    private Long cid2;
    @ApiModelProperty("三级类目id")
    private Long cid3;
    @ApiModelProperty("skuid集合(一次最多支持查询100个sku)，多个使用“,”分隔符")
    private String skuIds;
    @ApiModelProperty("关键词，字数同京东商品名称一致，目前未限制")
    private String keyword;
    @ApiModelProperty("商品券后价格下限")
    private BigDecimal priceFrom;
    @ApiModelProperty("商品券后价格上限")
    private BigDecimal priceTo;
    @ApiModelProperty("佣金比例区间开始")
    private String commissionShareStart;
    @ApiModelProperty("佣金比例区间结束")
    private String commissionShareEnd;
    @ApiModelProperty("商品类型：自营[g]，POP[p]")
    private String owner;
    @ApiModelProperty("排序字段(price：单价, commissionShare：佣金比例, commission：佣金， inOrderCount30Days：30天引单量， inOrderComm30Days：30天支出佣金)")
    private String sortName;
    @ApiModelProperty("asc：升序；desc：降序。默认降序")
    private String sort;
    @ApiModelProperty("是否是优惠券商品，1：有优惠券，0：无优惠券")
    private Integer isCoupon;
    @ApiModelProperty("拼购价格区间开始")
    private BigDecimal pingouPriceStart;
    @ApiModelProperty("拼购价格区间结束")
    private BigDecimal pingouPriceEnd;
    @ApiModelProperty("品牌code")
    private String brandCode;
    @ApiModelProperty("店铺Id")
    private Integer shopId;
    @ApiModelProperty("1：查询有最优惠券商品；其他值过滤掉此入参条件。（查询最优券需与isCoupon同时使用）")
    private Integer hasBestCoupon;
    @ApiModelProperty("联盟id_应用iD_推广位id")
    private String pid;
    @ApiModelProperty("京喜商品类型，1京喜、2京喜工厂直供、3京喜优选（包含3时可在京东APP购买），入参多个值表示或条件查询")
    private String jxFlags;
    @ApiModelProperty("主商品spuId")
    private Long spuId;
    @ApiModelProperty("优惠券链接")
    private String couponUrl;
    @ApiModelProperty("京东配送 1：是，0：不是")
    private Integer deliveryType;
    @ApiModelProperty("京东联盟搜索请求path")
    private final String requestPath = "/dels/jd/goods/search";


    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkJdUnionQueryResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkJdUnionQueryResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
