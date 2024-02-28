package com.mailvor.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 淘宝订单绑定用户
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Data
@Component
public class TaskConfig implements InitializingBean {
    @Value("${quartz.main}")
    private Boolean quartzMain;

    public static Boolean QUARTZ_MAIN;

    @Override
    public void afterPropertiesSet() {
        QUARTZ_MAIN = quartzMain;
    }
}
