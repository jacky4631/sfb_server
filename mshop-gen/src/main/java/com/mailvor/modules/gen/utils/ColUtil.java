/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.gen.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * sql字段转java
 *
 * @author Zheng Jie
 * @date 2019-01-03
 */
public class ColUtil {

    /**
     * 转换mysql数据类型为java数据类型
     * @param type 数据库字段类型
     * @return String
     */
    static String cloToJava(String type){
        Configuration config = getConfig();
        assert config != null;
        return config.getString(type,"unknowType");
    }

    /**
     * 获取配置信息
     */
    public static PropertiesConfiguration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties" );
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
