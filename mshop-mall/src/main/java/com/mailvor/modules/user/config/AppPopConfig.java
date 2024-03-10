package com.mailvor.modules.user.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * app首页弹窗配置，一般用户活动红包领取，目前填固定链接，不做分销，后期优化
 * */
@Data
public class AppPopConfig implements Serializable {

    /**
     * 红包背景图
     * */
    @NotBlank
    private String img = "";

    /**
     * 跳转链接，一般是转链过后的链接，可以自动调整淘宝app等
     * */
    @NotBlank
    private String url = "";


    /**
     * webview打开的链接，特殊情况下无法调用url跳转第三方app时，会调用内部webview打开链接
     * */
    private String webUrl;
    /**
     * webview打开的链接，特殊情况下无法调用url跳转第三方app时，会调用内部webview打开链接
     * */
    private String title;

    /**
     * webview标题颜色，特殊情况下无法调用url跳转第三方app时，会调用内部webview打开链接
     * 例如：9A34FF 不要带#
     * */
    private String color;
}
