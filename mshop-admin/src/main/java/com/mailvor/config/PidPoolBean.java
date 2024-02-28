package com.mailvor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 淘宝订单绑定用户
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "tb.pid")
public class PidPoolBean {
    private List<String> pool;
}
