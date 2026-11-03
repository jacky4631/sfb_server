package com.mailvor.modules.tk.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "抖音搜索商品vo")
@Data
public class DyCmsListDataVO {
    @JsonProperty("dy_logo")
    @Schema(description = "描述")
    private String dyLogo;
    @JsonProperty("min_id")
    @Schema(description = "商品码")
    private Integer minId;
    @Schema(description = "商品数据")
    private List<DySearchVO> products;
}
