package com.mailvor.modules.tk.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 商品表 查询结果对象
 * </p>
 *
 * @author shenji
 * @date 2019-10-19
 */
@AllArgsConstructor
@Data
@ApiModel(value = "PddNavVo", description = "商品表查询参数")
public class HotWordsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关键词")
    private String keyword;
}
