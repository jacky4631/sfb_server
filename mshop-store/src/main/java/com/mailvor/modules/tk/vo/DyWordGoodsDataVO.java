package com.mailvor.modules.tk.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "抖音转链vo")
@Data
public class DyWordGoodsDataVO {

    @Schema(description = "code")
    private Integer code;

    @Schema(description = "msg")
    private String msg;

    @Schema(description = "转链数据")
    private DyWordGoodsVO data;
}
