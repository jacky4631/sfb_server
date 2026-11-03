package com.mailvor.common.config;

import cn.hutool.core.util.StrUtil;
import com.mailvor.xss.XssFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.DispatcherType;
import java.util.HashMap;
import java.util.Map;


/**
 * @ClassName Filter配置
 * @author huangyu
 * @Date 2020/4/30
 **/
@Configuration(proxyBeanMethods = false)
public class FilterConfig
{
    @Value("${xss.enabled}")
    private String enabled;

    @Value("${xss.excludes}")
    private String excludes;

    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean xssFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        // hutool 5.8 的 StrUtil.split 返回 List，addUrlPatterns 需要 String...
        registration.addUrlPatterns(StrUtil.splitToArray(urlPatterns, ','));
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("excludes", excludes);
        initParameters.put("enabled", enabled);
        registration.setInitParameters(initParameters);
        return registration;
    }
}
