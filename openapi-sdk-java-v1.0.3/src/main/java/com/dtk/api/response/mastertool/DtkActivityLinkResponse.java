package com.dtk.api.response.mastertool;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 官方活动会场转链响应结果实体
 *
 * @author:YSH
 * @date: 2021/7/05
 * @time: 09:57
 */
@Getter
@Setter
public class DtkActivityLinkResponse {
    @JsonAlias("page_name")
    private String pageName;
    @JsonAlias("click_url")
    private String clickUrl;
    @JsonProperty("Tpwd")
    private String tpwd;
    @JsonAlias("longTpwd")
    private String longTpwd;
    @JsonAlias("terminal_type")
    private String terminalType;
    @JsonAlias("page_start_time")
    private String pageStartTime;
    @JsonAlias("page_end_time")
    private String pageEndTime;
    @JsonAlias("wx_qrcode_url")
    private String wxQrcodeUrl;
    @JsonAlias("wx_miniprogram_path")
    private String wxMiniProgramPath;
}
