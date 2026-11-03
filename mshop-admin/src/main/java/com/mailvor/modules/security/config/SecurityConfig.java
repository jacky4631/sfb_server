/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.security.config;

import com.mailvor.annotation.AnonymousAccess;
import com.mailvor.modules.security.security.JwtAccessDeniedHandler;
import com.mailvor.modules.security.security.JwtAuthenticationEntryPoint;
import com.mailvor.modules.security.security.TokenFilter;
import com.mailvor.modules.security.security.TokenUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author huangyu
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final TokenUtil tokenUtil;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ApplicationContext applicationContext;

    public SecurityConfig(TokenUtil tokenUtil, CorsFilter corsFilter, JwtAuthenticationEntryPoint authenticationErrorHandler, JwtAccessDeniedHandler jwtAccessDeniedHandler, ApplicationContext applicationContext) {
        this.tokenUtil = tokenUtil;
        this.corsFilter = corsFilter;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.applicationContext = applicationContext;
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 去除 ROLE_ 前缀
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 密码加密方式
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 6 移除了 WebSecurityConfigurerAdapter，改为声明 SecurityFilterChain bean；
     * 原 TokenConfigurer（SecurityConfigurerAdapter）只剩“把 TokenFilter 插到用户名密码过滤器之前”，
     * 直接 addFilterBefore 即可，Configurer 类删除。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 搜寻匿名标记 url： @AnonymousAccess
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        Set<String> anonymousUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            if (null != anonymousAccess) {
                // Spring 6 起 PathPatternParser 是默认匹配策略，getPatternsCondition() 会返回 null，
                // 统一用 getPatternValues()
                anonymousUrls.addAll(infoEntry.getKey().getPatternValues());
            }
        }
        httpSecurity
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                // 授权异常
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationErrorHandler)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                // 不创建会话
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 防止 iframe 造成跨域
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(requests -> requests
                        // 放行 OPTIONS 请求（跳域预检）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 静态资源等等
                        .requestMatchers(HttpMethod.GET,
                                "/*.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/webSocket/**")
                        .permitAll()
                        // 文件
                        .requestMatchers("/avatar/**", "/file/**").permitAll()
                        // 自定义匿名访问所有url放行 ： 允许匿名和带权限以及登录用户访问
                        .requestMatchers(anonymousUrls.toArray(new String[0])).permitAll()
                        // 所有请求都需要认证
                        .anyRequest().authenticated());
        // 添加登陆认证
        httpSecurity.addFilterBefore(new TokenFilter(tokenUtil), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * swagger / knife4j / druid 等文档与监控资源完全绕过过滤链。
     * <p>
     * 原实现用 {@code antMatchers} 写在 authorizeRequests 链里；Spring 6 之后
     * {@code WebSecurity.ignoring()} 只能通过 WebSecurityCustomizer 声明，
     * 且这些路径含 {@code /**} 这类 Ant 风格模式，交给 requestMatchers 会被
     * PathPatternParser 拒绝，所以放在 ignoring 里。
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/v2/api-docs",
                "/v2/api-docs-ext",
                "/doc.html",
                "/webjars/**",
                "/druid/**",
                "/api/canvas/**");
    }
}
