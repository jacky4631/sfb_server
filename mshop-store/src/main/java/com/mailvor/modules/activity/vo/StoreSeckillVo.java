package com.mailvor.modules.activity.vo;


import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.vo.MwStoreProductAttrQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 秒杀产品表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-17
 */
@Data
@Builder
public class StoreSeckillVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀产品评论信息")
    private MwStoreProductReplyQueryVo reply;

    @ApiModelProperty(value = "秒杀产品评论数量")
    private Long replyCount;

    @ApiModelProperty(value = "秒杀产品信息")
    private MwStoreSeckillQueryVo storeInfo;

    @Builder.Default
    @ApiModelProperty(value = "秒杀产品用户是否收藏")
    private Boolean userCollect = false;

    @ApiModelProperty(value = "模板名称")
    private String tempName;

    private List<MwStoreProductAttrQueryVo> productAttr = new ArrayList();

    private Map<String, MwStoreProductAttrValue> productValue = new LinkedHashMap<>();

}
