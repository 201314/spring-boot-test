package com.gitee.linzl.commons.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.commons.fileupload.CustomMultipartResolver;
import com.gitee.linzl.commons.interceptor.DefaultPermissionTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * 可以通过实现WebMvcConfigurer,亦可继承WebMvcConfigurationSupport
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2019年2月28日
 */
@Configuration
@Slf4j
public class CommonConfiguration implements WebMvcConfigurer {
    @Profile({"dev"})
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (log.isDebugEnabled()) {
            log.debug("初始化CorsRegistry,允许开发环境dev跨域");
        }
        // 设置允许跨域的路径
        registry.addMapping("/**")
            // 设置允许跨域请求的域名
            .allowedOrigins("*")
            // 是否允许证书 不再默认开启
            .allowCredentials(true)
            // 设置允许的方法
            .allowedMethods("*").allowedMethods("*")
            // 跨域允许时间
            .maxAge(3600);
    }

    @Profile({"dev"})
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (log.isDebugEnabled()) {
            log.debug("CommonConfig:初始化ResourceHandlerRegistry");
        }
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 描述 : 国际化拦截器，配合SessionLocaleResolver或 CookieLocaleResolver使用
     * <p>
     * 如果是需要在界面上进行切换国际化，根据 url?local=国际化语言标识 自动国际化
     * <p>
     * 语言标识java.util.Locale
     *
     * @return
     */
    @Bean
    public HandlerInterceptor localeChangeInterceptor() {
        if (log.isDebugEnabled()) {
            log.debug("初始化国际化拦截器,url?local=语言标识");
        }
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        // 根据locale变量动态改变语言
        localeChangeInterceptor.setParamName(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
        // 忽略无效的语言
        localeChangeInterceptor.setIgnoreInvalidLocale(true);
        return localeChangeInterceptor;
    }

    @Bean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    /**
     * 描述 : 文件上传处理器
     *
     * @return
     */
    @Bean(DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public CustomMultipartResolver customMultipartResolver() {
        if (log.isDebugEnabled()) {
            log.debug("初始化文件上传,使用CustomMultipartResolver");
        }
        CustomMultipartResolver resolver = new CustomMultipartResolver();
        resolver.setDefaultEncoding(GlobalConstants.DEFAULT_CHARACTER_ENCODING);
        return resolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * Converter类型转换,在org.springframework.core.convert.ConversionService使用
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FormStringToDateConverter.StringToDateConverter());
        registry.addConverter(new FormStringToDateConverter.StringToLocalDateConverter());
        registry.addConverter(new FormStringToDateConverter.LongToLocalDateConverter());
        registry.addConverter(new FormStringToDateConverter.StringToLocalDateTimeConverter());
        registry.addConverter(new FormStringToDateConverter.LongToLocalDateTimeConverter());
    }

    @Bean
    public DefaultPermissionTokenInterceptor tokenInterceptor() {
        return new DefaultPermissionTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.tokenInterceptor()).addPathPatterns("/**")
            .excludePathPatterns("/swagger-resources/**",
                "/webjars/**", "/v2/**",
                "/swagger-ui.html/**",
                "/favicon.ico");
        registry.addInterceptor(this.localeChangeInterceptor()).addPathPatterns("/**");
    }
}
