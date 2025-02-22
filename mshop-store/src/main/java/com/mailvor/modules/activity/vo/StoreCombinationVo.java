package com.mailvor.modules.activity.vo;

import com.mailvor.modules.activity.service.dto.PinkDto;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.vo.MwStoreProductAttrQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 拼团产品表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-11-19
 */
@Data
public class StoreCombinationVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "拼团详情")
    private List<PinkDto> pink;

    @ApiModelProperty(value = "参与的拼团的id 集合")
    private List<Long> pindAll;

    @ApiModelProperty(value = "拼团成功的用户信息")
    private List<String> pinkOkList;

    @ApiModelProperty(value = "拼团完成的商品总件数")
    private Integer pinkOkSum;

    @ApiModelProperty(value = "拼团评论信息")
    private MwStoreProductReplyQueryVo reply;

    @ApiModelProperty(value = "拼团评论总条数")
    private Long replyCount;

    @ApiModelProperty(value = "拼团好评比例")
    private String replyChance;

    @ApiModelProperty(value = "拼团产品表信息")
    private MwStoreCombinationQueryVo storeInfo;

    @ApiModelProperty(value = "拼团产品用户是否收藏")
    private Boolean userCollect = false;

    @ApiModelProperty(value = "拼团产品运费模板名称")
    private String tempName;

    @ApiModelProperty(value = "拼团产品属性信息")
    private List<MwStoreProductAttrQueryVo> productAttr = new ArrayList();

    @ApiModelProperty(value = "拼团产品属性值")
    private Map<String, MwStoreProductAttrValue> productValue = new LinkedHashMap<>();

}
