/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * api页面 /doc.html
 * <p>
 * 原实现基于 springfox 的 Docket，springfox 在 Jakarta / Spring Boot 3+ 上已不可用，
 * 这里改为 springdoc-openapi + knife4j（jakarta 版）。
 * <p>
 * 与旧实现的对应关系：
 * <ul>
 *     <li>{@code Docket.enable(enabled)} -&gt; {@code @ConditionalOnProperty(swagger.enabled)}，
 *         同时 yml 里把 {@code springdoc.api-docs.enabled} 指向同一个开关，保证文档端点也一起关掉</li>
 *     <li>{@code RequestHandlerSelectors.basePackage} -&gt; {@code packagesToScan}</li>
 *     <li>{@code PathSelectors.regex("/error.*").negate()} -&gt; {@code pathsToExclude}</li>
 *     <li>{@code globalOperationParameters(token 头)} -&gt; {@link OperationCustomizer}</li>
 *     <li>{@code securitySchemes/securityContexts} -&gt; 全局 {@code SecurityScheme} + {@code SecurityRequirement}。
 *         旧实现用 {@code ^(?!/auth).*$} 排除 auth 接口，springdoc 没有等价的路径级开关；
 *         这里保留"所有接口声明同一个 header"的行为，鉴权本身仍由 Spring Security 决定，
 *         文档里多出的一个可选请求头不影响调用</li>
 *     <li>springfox 的 Pageable 备选类型规则（把 Pageable 换成 page/size/sort 三个字段）不再需要，
 *         springdoc 原生支持 Spring Data 分页参数</li>
 * </ul>
 *
 * @author huangyu
 * @Date 2019/1/9
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true")
public class SwaggerConfig {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.token-start-with}")
    private String tokenStartWith;

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.version}")
    private String version;

    @Value("${swagger.serverUrl}")
    private String serverUrl;

    @Bean
    public OpenAPI mshopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(title)
                        .version(version)
                        .termsOfService(serverUrl)
                        .contact(new Contact().name("wanglu").url("https://www.mailvor.com")))
                .servers(List.of(new Server().url(serverUrl)))
                .components(new Components().addSecuritySchemes(tokenHeader, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(tokenHeader)
                        .description("token")))
                .addSecurityItem(new SecurityRequirement().addList(tokenHeader));
    }

    /**
     * 只暴露业务模块下的接口，排除 /error
     */
    @Bean
    public GroupedOpenApi mshopApi() {
        return GroupedOpenApi.builder()
                .group("mshop")
                .packagesToScan("com.mailvor.modules")
                .pathsToExclude("/error.*")
                .build();
    }

    /**
     * 等价于旧的 globalOperationParameters：每个接口都带上 token 请求头，默认值 "Bearer "
     */
    @Bean
    public OperationCustomizer tokenHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            // swagger-models 2.x 的 Schema 构造器是 protected，需用具体子类
            StringSchema schema = new StringSchema();
            schema.setDefault(tokenStartWith + " ");
            operation.addParametersItem(new HeaderParameter()
                    .name(tokenHeader)
                    .description("token")
                    .required(true)
                    .schema(schema));
            return operation;
        };
    }
}
