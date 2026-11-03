package com.mailvor;

import com.mailvor.utils.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author mazhongjun
 * @date 2019/10/1 9:20:19
 */
@EnableAsync
@EnableTransactionManagement
@EnableCaching
@MapperScan(basePackages ={"com.mailvor.modules.*.service.mapper", "com.mailvor.config"})
// wx-java-miniapp starter 4.3.0 只靠 spring.factories 注册自动配置，Spring Boot 4 下本就不会生效，
// 而 exclude 非自动配置类会直接抛 IllegalStateException，因此只保留 DataSourceAutoConfiguration
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class AppServer {

    public static void main(String[] args) {
        SpringApplication.run(AppServer.class, args);

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
                        "\n迈沃电商系统移动端API启动成功 \n官网：https://www.mailvor.com 提供技术支持ﾞ  \n");
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
