package com.mailvor.modules.product.vo;

import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品dto
 * </p>
 *
 * @author huangyu
 * @date 2019-10-23
 */
@Data
public class ProductVo{

    @ApiModelProperty(value = "商品信息列表")
    private List<MwStoreProductQueryVo> goodList = new ArrayList();

    @ApiModelProperty(value = "商户ID，预留字段")
    private Integer merId = 0;

    private String priceName = "";

    private List<MwStoreProductAttrQueryVo> productAttr = new ArrayList();

    private Map<String, MwStoreProductAttrValue>  productValue = new LinkedHashMap<>();

    @ApiModelProperty(value = "评论信息")
    private MwStoreProductReplyQueryVo reply;

    @ApiModelProperty(value = "回复渠道")
    private String replyChance;

    @ApiModelProperty(value = "回复数")
    private Long replyCount;

    //todo
    private List similarity = new ArrayList();

    @ApiModelProperty(value = "商品信息")
    private MwStoreProductQueryVo storeInfo;

    @ApiModelProperty(value = "腾讯地图key")
    private String mapKey;

    @ApiModelProperty(value = "门店信息")
    private MwSystemStoreQueryVo systemStore;

    @ApiModelProperty(value = "用户ID")
    private Integer uid = 0;

    @ApiModelProperty(value = "模版名称")
    private String tempName;

}
