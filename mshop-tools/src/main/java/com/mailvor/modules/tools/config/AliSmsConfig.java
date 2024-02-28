package com.mailvor.modules.tools.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AliSmsConfig implements InitializingBean {
    //读取配置文件内容
    @Value("${aliyun.sms.accessKey}")
    private String accessKey;
    @Value("${aliyun.sms.accessSecret}")
    private String accessSecret;
    @Value("${aliyun.sms.sign}")
    private String sign;
    @Value("${aliyun.sms.templateId}")
    private String templateId;
    public static String SMS_ACCESS_KEY;
    public static String SMS_ACCESS_SECRET;
    public static String SMS_SIGN;
    public static String SMS_TEMPLATE_ID;

    @Override
    public void afterPropertiesSet() throws Exception {
        SMS_ACCESS_KEY = accessKey;
        SMS_ACCESS_SECRET = accessSecret;
        SMS_SIGN = sign;
        SMS_TEMPLATE_ID = templateId;
    }
}
