package com.mailvor.modules.tk.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "剪切板识别商品vo")
@Data
public class TkParseCodeVO {

    @ApiModelProperty(value = "描述")
    private String msg = "成功";

    @ApiModelProperty(value = "商品码")
    private Integer code = -1;

    @ApiModelProperty(value = "msg")
    private TkParseVO data;
}
