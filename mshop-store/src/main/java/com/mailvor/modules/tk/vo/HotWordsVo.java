package com.mailvor.modules.tk.vo;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "商品表查询参数")
public class HotWordsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "关键词")
    private String keyword;
}
