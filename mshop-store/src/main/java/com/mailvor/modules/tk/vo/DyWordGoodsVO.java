package com.mailvor.modules.tk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "转链vo")
@Data
public class DyWordGoodsVO {

    @JSONField(name = "dy_zlink")
    @Schema(description = "转链过后的链接")
    private String link;

    @JSONField(name = "dy_deeplink")
    @Schema(description = "转链过后的链接")
    private String longLink;
    @JSONField(name = "dy_password")
    @Schema(description = "短口令")
    private String tpwd;

}
