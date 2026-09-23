package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.mastertool.DtkFirstOrderGiftMoneyResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

/**
 * 首单礼金商品请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:05
 */
@Getter
@Setter
public class DtkFirstOrderGiftMoneyRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkFirstOrderGiftMoneyResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.1.0")
    private String version = "v1.1.0";
    @ApiModelProperty(value = "大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”。1 -女装，2 -母婴，3 -美妆，4 -居家日用，5 -鞋品，6 -美食，7 -文娱车品，8 -数码家电，9 -男装，10 -内衣，11 -箱包，12 -配饰，13 -户外运动，14 -家装家纺")
    private String cids;
    @ApiModelProperty(value = "排序方式，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty(value = "输入关键词搜索")
    private String keyWord;
    @ApiModelProperty(value = "商品类型1表示大淘客商品2表示联盟商品。默认为1")
    private Integer goodsType;
    @ApiModelProperty("首单礼金商品请求请求path")
    private final String requestPath = "/goods/first-order-gift-money";

    @Override
    public TreeMap<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkFirstOrderGiftMoneyResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkFirstOrderGiftMoneyResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }

}
