package com.mailvor.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 需要全跑的task
 * @author Zheng Jie
 * @date 2018-12-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "quartz")
public class TaskFullRunConfig implements InitializingBean {
    private List<String> full;

    public static List<String> QUARTZ_FULL_LIST;

    @Override
    public void afterPropertiesSet() {
        QUARTZ_FULL_LIST = full;
    }
}
