package com.mailvor.modules.tk.vo.pdd;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一数据结构
 * */
@Schema(description = "转链vo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PddWordVO {

    @JSONField(name = "mobileShortUrl")
    @Schema(description = "转链过后的链接")
    private String link;

    @JSONField(name = "mobileUrl")
    @Schema(description = "转链过后的链接")
    private String longLink;

    @JSONField(name = "weAppPagePath")
    @Schema(description = "微信小程序路径")
    private String wechatPath;

    @Schema(description = "微信小程序原始id，app跳转微信小程序用")
    private String wechatId;

    @Schema(description = "微信小程序应用id，微信小程序内互跳用")
    private String wechatAppId;
}
