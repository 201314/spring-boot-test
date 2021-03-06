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
 * ??????????????????WebMvcConfigurer,????????????WebMvcConfigurationSupport
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2019???2???28???
 */
@Configuration
@Slf4j
public class CommonConfiguration implements WebMvcConfigurer {
    @Profile({"dev"})
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (log.isDebugEnabled()) {
            log.debug("?????????CorsRegistry,??????????????????dev??????");
        }
        // ???????????????????????????
        registry.addMapping("/**")
            // ?????????????????????????????????
            .allowedOrigins("*")
            // ?????????????????? ??????????????????
            .allowCredentials(true)
            // ?????????????????????
            .allowedMethods("*").allowedHeaders("*")
            // ??????????????????
            .maxAge(3600);
    }

    @Profile({"dev"})
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (log.isDebugEnabled()) {
            log.debug("CommonConfig:?????????ResourceHandlerRegistry");
        }
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * ?????? : ???????????????????????????SessionLocaleResolver??? CookieLocaleResolver??????
     * <p>
     * ????????????????????????????????????????????????????????? url?local=????????????????????? ???????????????
     * <p>
     * ????????????java.util.Locale
     *
     * @return
     */
    @Bean
    public HandlerInterceptor localeChangeInterceptor() {
        if (log.isDebugEnabled()) {
            log.debug("???????????????????????????,url?local=????????????");
        }
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        // ??????locale????????????????????????
        localeChangeInterceptor.setParamName(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
        // ?????????????????????
        localeChangeInterceptor.setIgnoreInvalidLocale(true);
        return localeChangeInterceptor;
    }

    @Bean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    /**
     * ?????? : ?????????????????????
     *
     * @return
     */
    @Bean(DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public CustomMultipartResolver customMultipartResolver() {
        if (log.isDebugEnabled()) {
            log.debug("?????????????????????,??????CustomMultipartResolver");
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
     * Converter????????????,???org.springframework.core.convert.ConversionService??????
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
