package com.mailvor.modules.tk.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "抖音分类vo")
@Data
public class DyCateVO {

    @JsonProperty("cat_id")
    @Schema(description = "分类id")
    private Integer cid;
    @JsonProperty("cat_name")
    @Schema(description = "分类标题")
    private String title;
}
