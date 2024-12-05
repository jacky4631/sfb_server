package com.mailvor.modules.tk.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "抖音搜索商品vo")
@Data
public class DyCmsListVO {

    @Schema(description = "描述")
    private String msg;
    @Schema(description = "商品码")
    private Integer code;
    @Schema(description = "商品数据")
    private DyCmsListDataVO data;
}
