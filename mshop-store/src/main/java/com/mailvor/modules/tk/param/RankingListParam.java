/**
 * Copyright (C) 2018-2021
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *
 * @author shenji
 * @date 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品表查询参数")
public class RankingListParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "类别")
    private Integer rankType;

    @Schema(description = "分类ID")
    private Integer cid;

    @Schema(description = "是否新品")
    private Integer pageSize;

    @Schema(description = "是否积分兑换商品")
    private Integer pageId;
}
