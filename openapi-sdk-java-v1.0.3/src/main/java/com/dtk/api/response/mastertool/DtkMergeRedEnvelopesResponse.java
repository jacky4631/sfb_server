package com.dtk.api.response.mastertool;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * 三合一红包接口响应结果实体
 *
 * @author 1
 * @date 2020/11/11 15:51
 */
@Getter
@Setter
public class DtkMergeRedEnvelopesResponse {
    private String link;
    @JsonAlias("Tpwd")
    private String tpwd;
    @JsonAlias("longTpwd")
    private String longTpwd;
    @JsonAlias("shortURL")
    private String shortURL;
    @JsonAlias("mobile_url")
    private String mobileUrl;
    @JsonAlias("schema_url")
    private String schemaUrl;
    @JsonAlias("short_url")
    private String shortUrl;
    @JsonAlias("we_app_web_view_short_url")
    private String weAppWebViewShortUrl;
    @JsonAlias("mobile_short_url")
    private String mobileShortUrl;
    @JsonAlias("we_app_web_view_url")
    private String weAppWebViewUrl;
    private String url;
}
