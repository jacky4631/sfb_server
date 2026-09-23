package com.dtk.api.controller.config;

import com.dtk.api.constant.DtkApiConstant;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * dtk api: SwaggerConfiguration
 *
 * @author baige
 * @date 2020/11/13 14:31
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfiguration {
    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .globalOperationParameters(getGlobalOperationParameters())
                //分组名称
                .groupName("开放平台api")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.dtk.api.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("开放平台接口文档").description("包含所有开放接口").version("v1.0.0").build();
    }

    private List<Parameter> getGlobalOperationParameters() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        parameterBuilder.name(DtkApiConstant.RequestCommonParam.APP_KEY).description("appKey").modelRef(new ModelRef("string")).parameterType("header").required(true);
        pars.add(parameterBuilder.build());

        parameterBuilder.name(DtkApiConstant.RequestCommonParam.APP_SECRET).description("appSecret").modelRef(new ModelRef("string")).parameterType("header").required(true);
        pars.add(parameterBuilder.build());
        return pars;
    }
}
