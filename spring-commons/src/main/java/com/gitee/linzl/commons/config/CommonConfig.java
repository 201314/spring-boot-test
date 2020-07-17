package com.gitee.linzl.commons.config;

import com.alibaba.fastjson.serializer.*;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.commons.fileupload.CustomMultipartResolver;
import com.gitee.linzl.commons.interceptor.DefaultPermissionTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
public class CommonConfig implements WebMvcConfigurer {
    private static final boolean fastJsonPresent;

    static {
        ClassLoader classLoader = WebMvcConfigurationSupport.class.getClassLoader();
        fastJsonPresent = ClassUtils.isPresent("com.alibaba.fastjson.JSON", classLoader);
    }

    /**
     * 添加一个自定义的HttpMessageConverter,不覆盖默认注册的HttpMessageConverter
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.debug("extendMessageConverters========>extendMessageConverters");
        if (fastJsonPresent) {
            FastJsonHttpMessageConverter jsonConverter = new FastJsonHttpMessageConverter();
            FastJsonConfig fastJsonConfig = new FastJsonConfig();

            SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
            // 支持 LocalDateTime 、LocalDate 转换成long
            LocalDateToLongSerializer instance = new LocalDateToLongSerializer();
            serializeConfig.put(LocalDateTime.class, instance);
            serializeConfig.put(LocalDate.class, instance);
            // 驼峰转下划线
            // FastJson配置驼峰命名规则会导致spring boot admin 或者 swagger-ui无法正常使用
            fastJsonConfig.setSerializeConfig(serializeConfig);

            // fastjson 统一返回时间戳,由前端自己处理格式
            // 空值特别处理
            // WriteNullListAsEmpty 将Collection类型字段的字段空值输出为[]
            // WriteNullStringAsEmpty 将字符串类型字段的空值输出为空字符串 ""
            // WriteNullNumberAsZero 将数值类型字段的空值输出为0
            // WriteNullBooleanAsFalse 将Boolean类型字段的空值输出为false
            fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.WriteMapNullValue);
            jsonConverter.setFastJsonConfig(fastJsonConfig);

            // 解决中文乱码问题
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            jsonConverter.setSupportedMediaTypes(mediaTypes);

            converters.add(0, jsonConverter);
            if (log.isDebugEnabled()) {
                log.debug("初始化fastjson,默认时间格式yyyy-MM-dd HH:mm:ss");
            }
        } else {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            // 忽略 transient 修饰的属性
            builder.featuresToEnable(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
            ObjectMapper objectMapper = builder.build();
            converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper));
            if (log.isDebugEnabled()) {
                log.debug("初始化JackSon,默认时间格式为long");
            }
        }
    }

    class LocalDateToLongSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            SerializeWriter out = serializer.out;
            if (object == null) {
                out.writeNull();
                return;
            }
            if (object instanceof LocalDateTime) {
                LocalDateTime time = (LocalDateTime) object;
                out.writeLong(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            if (object instanceof LocalDate) {
                LocalDate time = (LocalDate) object;
                out.writeLong(time.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
        }
    }


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
     * 描述 : 国际化拦截器
     * <p>
     * 如果是需要在界面上进行切换国际化，根据 url?local=国际化语言标识 自动国际化
     * <p>
     * 语言标识java.util.Locale
     *
     * @return
     */
    @Bean
    public HandlerInterceptorAdapter localeChangeInterceptor() {
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

    @Profile({"dev"})
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefaultPermissionTokenInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
//		registry.addInterceptor(new ControllerValidatorInterceptor()).addPathPatterns("/**");
    }

}
