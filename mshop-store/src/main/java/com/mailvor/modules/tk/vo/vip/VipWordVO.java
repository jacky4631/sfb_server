package com.mailvor.modules.tk.vo.vip;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一数据结构
 * */
@Schema(description = "转链vo")
@Data
public class VipWordVO {

    @JSONField(name = "deeplinkUrl")
    @Schema(description = "转链过后的链接")
    private String link;

    @JSONField(name = "longUrl")
    @Schema(description = "转链过后的链接")
    private String longLink;
    @JSONField(name = "vipWxUrl")
    @Schema(description = "微信小程序路径")
    private String wechatPath;


    private String url;

}
