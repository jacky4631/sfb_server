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


/**
 * 统一数据结构
 * */
@Schema(description = "京东转链vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdUnionCommonGoodsWordVO {

    @Schema(description = "转链url")
    private String link;
    @Schema(description = "口令")
    private String pwd;

}
