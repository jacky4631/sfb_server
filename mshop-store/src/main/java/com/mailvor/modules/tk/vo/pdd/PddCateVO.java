package com.mailvor.modules.tk.vo.pdd;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多分类vo")
@Data
public class PddCateVO {

    @JsonProperty("id")
    @Schema(description = "分类id")
    private Integer cid;
    @JsonProperty("name")
    @Schema(description = "分类标题")
    private String title;
}
