package com.mailvor.modules.tk.vo.jd;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一数据结构
 * */
@Schema(description = "京东商品vo")
@Data
public class JdGoodsDataVO {

    @Schema(description = "总数")
    @JsonProperty("totalNum")
    private Integer total;
    @Schema(description = "商品列表")
    private List<JdGoodsVO> list;
}
