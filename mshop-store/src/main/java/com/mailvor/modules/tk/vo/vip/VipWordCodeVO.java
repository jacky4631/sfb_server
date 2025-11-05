package com.mailvor.modules.tk.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "转链vo")
@Data
public class VipWordCodeVO {

    @Schema(description = "code")
    private Integer code;

    @Schema(description = "msg")
    private String msg;

    @Schema(description = "转链数据 元数据")
    private VipWordDataVO data;

    @Schema(description = "转链数据 新替换数据")
    private VipWordVO word;
}
