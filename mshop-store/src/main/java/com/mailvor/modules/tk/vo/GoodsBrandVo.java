package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 大淘客搜索商品 品牌列表对象（搜索结果商品所包含的所有品牌信息）
 * </p>
 *
 * @author shenji
 */
@Data
@ApiModel(value = "GoodsBrandVo对象", description = "大淘客搜索品牌信息")
public class GoodsBrandVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "品牌ID")
    private String brandId;

    @ApiModelProperty(value = "品牌logo地址")
    private String brandLogo;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;
}
