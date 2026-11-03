package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "大淘客搜索商品数据")
public class GoodsSearchDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品总数")
    private Integer totalNum;

    @Schema(description = "分页id，翻页时传入下一页")
    private String pageId;

    @Schema(description = "商品列表")
    private List<GoodsSearchVo> list;

    @Schema(description = "搜索结果商品所包含的所有品牌信息")
    private List<GoodsBrandVo> brandList;
}
