package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 大淘客搜索商品 data 层包装对象
 * 对应接口：/api/goods/get-dtk-search-goods
 * </p>
 *
 * @author shenji
 */
@Data
@ApiModel(value = "GoodsSearchDataVo对象", description = "大淘客搜索商品数据")
public class GoodsSearchDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "分页id，翻页时传入下一页")
    private String pageId;

    @ApiModelProperty(value = "商品列表")
    private List<GoodsSearchVo> list;

    @ApiModelProperty(value = "搜索结果商品所包含的所有品牌信息")
    private List<GoodsBrandVo> brandList;
}
