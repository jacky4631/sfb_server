package com.mailvor.modules.tk.vo.pdd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "拼多多分类vo")
@Data
public class PddCateVO {

    @JSONField(name = "id")
    @Schema(description = "分类id")
    private Integer cid;
    @JSONField(name = "name")
    @Schema(description = "分类标题")
    private String title;
}
