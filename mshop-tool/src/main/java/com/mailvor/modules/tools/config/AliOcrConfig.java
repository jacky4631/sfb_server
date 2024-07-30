package com.mailvor.modules.tools.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AliOcrConfig implements InitializingBean {
    //读取配置文件内容
    @Value("${aliyun.ocr.encryptKey}")
    private String encryptKey;
    @Value("${aliyun.ocr.path}")
    private String path;

    public static String ENCRYPT_KEY;
    public static String PATH;

    @Override
    public void afterPropertiesSet() throws Exception {
        ENCRYPT_KEY = encryptKey;
        PATH = path;
    }
}
