package com.gitee.linzl.autoconfigure;

import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.properties.SwaggerProperties;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @author linzhenlie
 * @date 2019/8/29
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
public class SwaggerAutoConfiguration implements WebMvcConfigurer {
    @Autowired
    private SwaggerProperties properties;

    /**
     * 创建API
     */
    @Bean
    public Docket createApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
                .apiInfo(apiInfo())
                .forCodeGeneration(true)
                // 设置哪些接口暴露给Swagger展示
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.any())
                // 扫描指定包中的swagger注解
                // .apis(RequestHandlerSelectors.basePackage("com.ruoyi.project.tool.swagger"))
                // 扫描所有 .apis(RequestHandlerSelectors.any())
                .paths(regex("^.*(?<!error)$"))
                .paths(PathSelectors.any())
                .build();

        if (properties.isGlobalToken()) {
            docket = docket.securitySchemes(securitySchemes())
                    .securityContexts(securityContexts());
        } else {
            ParameterBuilder tokenPar = new ParameterBuilder();

            tokenPar.name(GlobalConstants.ACCESS_TOKEN)
                    .description("令牌")
                    .defaultValue("testTokenUseEnglish")
                    .modelRef(new ModelRef("string"))
                    .parameterType("header")
                    .required(true)
                    .build();

            List<Parameter> pars = new ArrayList<>();
            pars.add(tokenPar.build());
            docket = docket.globalOperationParameters(pars);
        }
        return docket;
    }

    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList();
        apiKeyList.add(new ApiKey(GlobalConstants.ACCESS_TOKEN, GlobalConstants.ACCESS_TOKEN, "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{
                new AuthorizationScope("global", "accessEverything")
        };
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference(GlobalConstants.ACCESS_TOKEN, authorizationScopes));
        return securityReferences;
    }

    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo() {
        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
                // 设置标题
                .title(properties.getTitle())
                // 描述
                .description(properties.getDescription())
                // 作者信息
                .contact(new Contact(properties.getName(), properties.getUrl(), properties.getEmail()))
                // 版本
                .version(properties.getVersion()).build();
    }
}
