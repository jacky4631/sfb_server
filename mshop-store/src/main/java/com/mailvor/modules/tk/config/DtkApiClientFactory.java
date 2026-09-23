package com.mailvor.modules.tk.config;

import com.dtk.api.client.DtkApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 大淘客API客户端工厂
 * 基于官方SDK创建和管理DtkApiClient实例
 *
 * @author mailvor
 */
@Slf4j
@Configuration
public class DtkApiClientFactory {

    @Resource
    private DataokeConfig dataokeConfig;

    /**
     * 创建大淘客API客户端单例
     * 使用官方SDK的DtkApiClient.getInstance()方法
     *
     * @return DtkApiClient实例
     */
    @Bean
    public DtkApiClient dtkApiClient() {
        log.info("初始化大淘客API客户端, appKey: {}", dataokeConfig.getKey());
        
        // 验证配置
        if (dataokeConfig.getKey() == null || dataokeConfig.getKey().trim().isEmpty()) {
            throw new IllegalStateException("大淘客appKey未配置，请在application.yml中配置dataoke.key");
        }
        if (dataokeConfig.getSecret() == null || dataokeConfig.getSecret().trim().isEmpty()) {
            throw new IllegalStateException("大淘客appSecret未配置，请在application.yml中配置dataoke.secret");
        }

        // 创建客户端实例（SDK内部已实现单例）
        return DtkApiClient.getInstance(dataokeConfig.getKey(), dataokeConfig.getSecret());
    }
}
