package com.mailvor.modules.user.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ShopConfig implements InitializingBean {
    //读取配置文件内容
    @Value("${shop.contractUrl}")
    private String contractUrl;
    @Value("${shop.sealUrl}")
    private String sealUrl;
    @Value("${shop.licenseUrl}")
    private String licenseUrl;
    @Value("${shop.localPath}")
    private String localPath;
    @Value("${shop.convertContract}")
    private Boolean convertContract;

    public static String CONTRACT_URL;
    public static String SEAL_URL;
    public static String LICENSE_URL;
    public static String LOCAL_PATH;
    public static Boolean CONVERT_CONTRACT;

    @Override
    public void afterPropertiesSet() throws Exception {
        CONTRACT_URL = contractUrl;
        SEAL_URL = sealUrl;
        LICENSE_URL = licenseUrl;
        LOCAL_PATH = localPath;
        CONVERT_CONTRACT = convertContract;
    }
}
