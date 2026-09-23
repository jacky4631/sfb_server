package com.dtk.api.request.mastertool;

import com.dtk.api.client.DtkApiRequest;
import com.dtk.api.response.base.DtkApiResponse;
import com.dtk.api.response.mastertool.DtkGoodsCommentListResponse;
import com.dtk.api.utils.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 商品评论请求参数实体
 *
 * @author baige
 * @date 2020/11/30 10:28
 */
@Getter
@Setter
public class DtkGoodsCommentListRequest implements DtkApiRequest<DtkApiResponse<DtkGoodsCommentListResponse>> {
    @ApiModelProperty(value = "版本号", example = "v1.0.0")
    private String version = "v1.0.0";
    @ApiModelProperty(value = "大淘客商品id（id和goodsid其中一个必填）")
    private Integer id;
    @ApiModelProperty(value = "淘宝商品id（id和goodsid其中一个必填）")
    private String goodsId;
    @ApiModelProperty(value = "默认：0-全部 评论类型：0-全部；1-含图；2-含视频")
    private Integer type;
    @ApiModelProperty(value = "默认：0-全部 评论类型：0-全部；1-含图；2-含视频")
    private Integer sort;
    @ApiModelProperty(value = "评论类型 0-全部 1-去掉默认好评 默认为0(2020/12/30新增字段)")
    private Integer haopingType;
    @ApiModelProperty("商品评论请求path")
    private final String requestPath = "/comment/get-comment-list";

    @Override
    public Map<String, String> getTextParams() throws IllegalAccessException {
        return ObjectUtil.objToMap(this);
    }

    @Override
    public String apiVersion() {
        return this.version;
    }

    @Override
    public TypeReference<DtkApiResponse<DtkGoodsCommentListResponse>> responseType() {
        return new TypeReference<DtkApiResponse<DtkGoodsCommentListResponse>>() {
        };
    }

    @Override
    public String requestUrl() {
        return this.requestPath;
    }

}
