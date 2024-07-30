package com.mailvor.modules.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RSA密钥
 * @author Zheng Jie
 * @date 2024-5-26
 */
@Data
@Component
public class RSAConfig implements InitializingBean {
    @Value("${rsa.private_key}")
    private String privateKey;
    public static String KEY_RSA_PRIVATE;

    @Override
    public void afterPropertiesSet() {
        KEY_RSA_PRIVATE = privateKey;
    }
}
