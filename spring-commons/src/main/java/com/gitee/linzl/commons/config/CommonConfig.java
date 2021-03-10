package com.gitee.linzl.commons.config;

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
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
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
    private static final boolean jackSonPresent;
    private static final boolean fastJsonPresent;

    static {
        ClassLoader classLoader = WebMvcConfigurationSupport.class.getClassLoader();
        fastJsonPresent = ClassUtils.isPresent("com.alibaba.fastjson.JSON", classLoader);
        jackSonPresent = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader);
    }

    /**
     * 添加一个自定义的HttpMessageConverter,不覆盖默认注册的HttpMessageConverter
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.debug("extendMessageConverters========>extendMessageConverters");
        if (jackSonPresent) {
            //如果有JackSon,优先使用
            return;
        }

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
        }
    }

    public class LocalDateToLongSerializer implements ObjectSerializer {
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

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FormStringToDateConverter.StringToDateConverter());
        registry.addConverter(new FormStringToDateConverter.StringToLocalDateConverter());
        registry.addConverter(new FormStringToDateConverter.StringToLocalDateTimeConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         *  描述 : 国际化拦截器
         *  如果是需要在界面上进行切换国际化，根据 url?local=国际化语言标识 自动国际化
         *  语言标识java.util.Locale
         */
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        // 根据locale变量动态改变语言
        localeChangeInterceptor.setParamName(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
        // 忽略无效的语言
        localeChangeInterceptor.setIgnoreInvalidLocale(true);
        registry.addInterceptor(localeChangeInterceptor);

        registry.addInterceptor(new DefaultPermissionTokenInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }

    /**
     * Slow http 拒绝服务原理:
     * 请求以很低的速度发送post请求数据包，当客户端连接了许多以后，占用了所有webserver可用连接，从而导致服务夯死。
     * http慢速攻击是利用http合法机制，在建立连接后，尽量长时间保持连接，不释放，从而达到对HTTP服务攻击,
     * 攻击者发送POST请求，自行构造报文向服务器提交数据，将报文长度设置一个很大的值，且在随后每次发送中，每次只发送一个很小的报文，这样导致服务器一直等待数据，连接始终一直被占用。
     * 如果攻击者使用多线程或傀儡机子去做同样操作，服务器WEB容器很快就被占满TCP连接从而不再接受新请求。
     */
    @ConditionalOnClass({Tomcat.class, UpgradeProtocol.class})
    @Bean
    public TomcatConnectorCustomizer tomcatConnectorCustomizer() {
        return new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                ProtocolHandler handler = (ProtocolHandler) connector.getProtocolHandler();
                if (handler instanceof AbstractProtocol) {
                    AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
                    //设置最大连接数
                    protocol.setMaxConnections(2000);
                    //设置最大线程数
                    protocol.setMaxThreads(2000);
                    protocol.setConnectionTimeout(30000);
                }
            }
        };
    }
}
