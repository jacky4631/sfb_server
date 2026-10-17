package com.mailvor.modules.tk.vo.pdd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多分类vo")
@Data
public class PddCateDataVO {

    @Schema(description = "code")
    private Integer code;

    @Schema(description = "msg")
    private String msg;

    @Schema(description = "转链数据")
    private List<PddCateVO> data;
}
