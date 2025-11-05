/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.RankGoodsQueryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 统一数据结构
 * */
@Schema(description = "京东商品vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdUnionCommonGoodsListVO {

    @Schema(description = "描述")
    private String msg;
    @Schema(description = "商品码")
    private Integer code;
    @Schema(description = "商品数据")
    private JdUnionGoodsDataVO data;

    public static JdUnionCommonGoodsListVO convert(RankGoodsQueryResult result) {
        JdUnionCommonGoodsListVO searchVO = JdUnionCommonGoodsListVO.builder()
                .code(result.getCode())
                .msg(result.getMessage())
                .build();
        searchVO.setData(JdUnionGoodsDataVO.builder()
                .list(JdUnionSearchVO.convert(result.getData()))
                .total(result.getTotalCount()).build());
        return searchVO;
    }
}
