/**
 * Copyright (C) 2018-2025
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.vo.jd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "商品通用vo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdKuGoodsCateInfoVO {
    /**
     * {
     *   "data" : [ {
     *     "category_info" : {
     *       "cid1Name" : "数码",
     *       "cid2Name" : "数码配件",
     *       "cid2" : 829,
     *       "cid3Name" : "电池/充电器",
     *       "cid3" : 854,
     *       "cid1" : 652
     *     }
     *   } ]
     * }
     * */
    @JSONField(name = "cid3Name")
    @Schema(description = "分类")
    private String cateName;

}
