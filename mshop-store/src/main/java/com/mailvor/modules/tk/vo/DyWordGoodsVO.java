package com.mailvor.modules.tk.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "转链vo")
@Data
public class DyWordGoodsVO {

    @JsonProperty("dy_zlink")
    @Schema(description = "转链过后的链接")
    private String link;

    @JsonProperty("dy_deeplink")
    @Schema(description = "转链过后的链接")
    private String longLink;
    @JsonProperty("dy_password")
    @Schema(description = "短口令")
    private String tpwd;

}
