package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.request.base.DtkPageParamRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.base.DtkPageResponse;
import com.dtk.api.response.mastertool.DtkGetCollectionListItemResponse;
import com.dtk.api.utils.ObjectUtil;
import com.dtk.api.utils.RequiredCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 我的收藏请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:07
 */
@Getter
@Setter
public class DtkGetCollectionListRequest extends DtkPageParamRequest implements DtkApiRequest<DtkApiResponse<DtkPageResponse<DtkGetCollectionListItemResponse>>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.1")
    private String version = "v1.0.1";
    @RequiredCheck
    @ApiModelProperty(value = "商品在大淘客的一级分类id，如果需要传多个，以英文逗号相隔，如：”1,2,3”。当一级类目id和二级类目id同时传入时，会自动忽略二级类目id，1 -女装，2 -母婴，3 " +
            "-美妆，4 -居家日用，5 -鞋品，6 -美食，7 -文娱车品，8 -数码家电，9 -男装，10 -内衣，11 -箱包，12 -配饰，13 -户外运动，14 -家装家纺", required = true)
    private String cid;
    @ApiModelProperty(value = "是否返回预告商品，1为预告商品，0为在线商品，不填则全部商品")
    private Integer trailerType;
    @ApiModelProperty(value = "排序字段，默认为0，0-综合排序，1-商品上架时间从高到低，2-销量从高到低，3-领券量从高到低，4-佣金比例从高到低，5-价格（券后价）从高到低，6-价格（券后价）从低到高")
    private String sort;
    @ApiModelProperty(value = "加入收藏时间")
    private Integer collectionTimeOrder;
    @ApiModelProperty("我的收藏请求path")
    private final String requestPath = "/goods/get-collection-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPageResponse<DtkGetCollectionListItemResponse>>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPageResponse<DtkGetCollectionListItemResponse>>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
