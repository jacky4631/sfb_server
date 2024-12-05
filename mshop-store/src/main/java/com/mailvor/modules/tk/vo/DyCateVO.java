package com.mailvor.modules.tk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "抖音分类vo")
@Data
public class DyCateVO {

    @JSONField(name = "cat_id")
    @Schema(description = "分类id")
    private Integer cid;
    @JSONField(name = "cat_name")
    @Schema(description = "分类标题")
    private String title;
}
