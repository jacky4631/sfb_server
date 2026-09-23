package com.dtk.api.request.search;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.search.DtkPddUnionQueryResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 拼多多联盟搜索请求参数实体
 *
 * @author 1
 * @date 2020/11/10 18:10
 */
@Getter
@Setter
public class DtkPddUnionQueryRequest implements DtkApiRequest<DtkApiResponse<DtkPddUnionQueryResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty("活动商品标记数组")
    private String activityTags;
    @ApiModelProperty("自定义屏蔽一级/二级/三级类目ID，自定义数量不超过20个;")
    private String blockCats;
    @ApiModelProperty("屏蔽商品类目包：1-拼多多小程序屏蔽的类目&关键词;2-虚拟类目;3-医疗器械;4-处方药;5-非处方药")
    private String blockCatPackages;
    @ApiModelProperty("商品类目ID")
    private Integer catId;
    @ApiModelProperty("商品goodsSign列表")
    private String goodsSignList;
    @ApiModelProperty("是否为品牌商品")
    private Integer isBrandGoods;
    @ApiModelProperty("商品关键词")
    private String keyword;
    @ApiModelProperty("翻页时建议填写前页返回的list_id值")
    private String listId;
    @ApiModelProperty("店铺类型数组 1-个人，2-企业，3-旗舰店，4-专卖店，5-专营店，6-普通店（未传为全部）")
    private String merchantTypeList;
    @ApiModelProperty("商品标签类目ID")
    private Integer optId;
    @ApiModelProperty("默认值1，商品分页数")
    private Integer page;
    @ApiModelProperty("默认100，每页商品数量")
    private Integer pageSize;
    @ApiModelProperty("筛选范围列表")
    private String rangeList;
    @ApiModelProperty("排序方式:0-综合排序;2-按佣金比例降序;3-按价格升序;4-按价格降序;6-按销量降序;9-券后价升序排序;10-券后价降序排序;16-店铺描述评分降序")
    private Integer sortType;
    @ApiModelProperty("是否只返回优惠券的商品，0返回所有商品，1只返回有优惠券的商品")
    private Integer withCoupon;
    @ApiModelProperty("是否返回分类信息数据 0-否；1-是")
    private Integer withCategoryInfo;
    @ApiModelProperty("拼多多推广位id（建议填写，可获取对应账号等级的佣金比例）")
    private String pid;
    @ApiModelProperty("自定义参数，为链接打上自定义标签；自定义参数最长限制64个字节")
    private String customParameters;
    @ApiModelProperty("拼多多联盟搜索请求path")
    private final String requestPath = "/dels/pdd/goods/search";


    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkPddUnionQueryResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkPddUnionQueryResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }
}
