package com.mailvor.modules.tk.vo.vip;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "转链vo")
@Data
public class VipWordVO {

    @JsonProperty("deeplinkUrl")
    @Schema(description = "转链过后的链接")
    private String link;

    @JsonProperty("longUrl")
    @Schema(description = "转链过后的链接")
    private String longLink;
    @JsonProperty("vipWxUrl")
    @Schema(description = "微信小程序路径")
    private String wechatPath;


    private String url;

}
