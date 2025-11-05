/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "京东商品vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdUnionGoodsDataVO {
    /**
     * 给一个默认值 兼容
     * */
    @Schema(description = "总数")
    private Long total;

    @Schema(description = "商品列表")
    private List<JdUnionSearchVO> list;
}
