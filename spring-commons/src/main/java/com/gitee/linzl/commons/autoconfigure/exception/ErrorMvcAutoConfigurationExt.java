package com.gitee.linzl.commons.autoconfigure.exception;

import javax.servlet.Servlet;
import java.util.stream.Collectors;

import com.gitee.springboot.autoconfigure.web.servlet.error.BasicErrorControllerExt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 参照 org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration 修改
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2018年10月2日
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class, WebMvcProperties.class })
public class ErrorMvcAutoConfigurationExt {
    private final ServerProperties serverProperties;

    public ErrorMvcAutoConfigurationExt(ServerProperties serverProperties,
                                        ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        this.serverProperties = serverProperties;
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public BasicErrorControllerExt basicErrorControllerExt(ErrorAttributes errorAttributes,
                                                           ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new BasicErrorControllerExt(errorAttributes, this.serverProperties.getError(), errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }
}
