package com.mailvor.modules.tk.vo.pdd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多搜索商品vo")
@Data
public class PddSearchListVO {

    @Schema(description = "描述")
    private String msg;
    @Schema(description = "商品码")
    private Integer code;
    @Schema(description = "商品数据")
    private PddSearchDataVO data;
}
