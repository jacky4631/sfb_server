package com.mailvor.modules.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
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

    @Schema(description = "商品信息列表")
    private List<MwStoreProductQueryVo> goodList = new ArrayList();

    @Schema(description = "商户ID，预留字段")
    private Integer merId = 0;

    private String priceName = "";

    private List<MwStoreProductAttrQueryVo> productAttr = new ArrayList();

    private Map<String, MwStoreProductAttrValue>  productValue = new LinkedHashMap<>();

    @Schema(description = "评论信息")
    private MwStoreProductReplyQueryVo reply;

    @Schema(description = "回复渠道")
    private String replyChance;

    @Schema(description = "回复数")
    private Long replyCount;

    //todo
    private List similarity = new ArrayList();

    @Schema(description = "商品信息")
    private MwStoreProductQueryVo storeInfo;

    @Schema(description = "腾讯地图key")
    private String mapKey;

    @Schema(description = "门店信息")
    private MwSystemStoreQueryVo systemStore;

    @Schema(description = "用户ID")
    private Integer uid = 0;

    @Schema(description = "模版名称")
    private String tempName;

}
