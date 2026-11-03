/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor;

import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.utils.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangyu
 * @date 2018/11/15 9:20:19
 */
@EnableAsync
@RestController
// wx-java-miniapp starter 4.3.0 只靠 spring.factories 注册自动配置，Spring Boot 4 下本就不会生效，
// 而 exclude 非自动配置类会直接抛 IllegalStateException，因此去掉 exclude 与 import；
// 小程序相关 bean 由 mshop-mp 自己的 WxMaConfiguration 提供
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages ={"com.mailvor.modules.*.service.mapper", "com.mailvor.config"})
public class AdminServer {

    public static void main(String[] args) {
        SpringApplication.run(AdminServer.class, args);
        System.out.println(
                        "                     **                              \n" +
                        "                     **                              \n" +
                        "                     **                              \n" +
                        "   * ** ***   ****   ** ***     ****   ** ***        \n"+
                        "   ** *****  **  **  **** **   **  **  ***  **       \n" +
                        "   *  **  *  *       **    *  **    *  **    *       \n" +
                        "   *  **  *  ***     **    *  **    ** **    *       \n" +
                        "   *  **  *    ****  **    *  **    ** **    *       \n" +
                        "   *  **  *       *  **    *  **    ** **    *       \n"+
                        "   *  **  *  *    *  **    *   **  **  **   **       \n" +
                        "   *  **  *  ******  **    *    ****   ******        \n" +
                        "                                       **            \n"+
                        "                                       **            \n"+
                        "                                       **            \n"+
                    "\n迈沃电商系统管理后台启动成功 \n官网：https://www.mailvor.com 提供技术支持ﾞ  \n");
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
        fa.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
        return fa;
    }

    /**
     * 访问首页提示
     * @return /
     */
    @GetMapping("/")
    @AnonymousAccess
    public String index() {
        return "Backend service started successfully";
    }
}
