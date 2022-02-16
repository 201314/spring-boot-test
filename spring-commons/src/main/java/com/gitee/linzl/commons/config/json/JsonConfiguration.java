package com.gitee.linzl.commons.config.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author linzhenlie-jk
 * @date 2021/11/5
 */
@Configuration
@Slf4j
public class JsonConfiguration implements WebMvcConfigurer {
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
            log.debug("如果有JackSon,优先使用");
            return;
        }

        if (fastJsonPresent) {
            SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
            // 序列化: 安卓环境下默认关闭
            serializeConfig.setAsmEnable(Boolean.TRUE);

            // 支持 LocalDateTime 、LocalDate 转换成long
            LocalDateToLongSerializer instance = new LocalDateToLongSerializer();
            serializeConfig.put(LocalDateTime.class, instance);
            serializeConfig.put(LocalDate.class, instance);

            FastJsonConfig fastJsonConfig = new FastJsonConfig();
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

            FastJsonHttpMessageConverter jsonConverter = new FastJsonHttpMessageConverter();
            jsonConverter.setFastJsonConfig(fastJsonConfig);

            // 解决中文乱码问题
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            jsonConverter.setSupportedMediaTypes(mediaTypes);

            converters.add(0, jsonConverter);

            // 反序列化: 安卓环境下默认关闭
            ParserConfig.getGlobalInstance().setAsmEnable(Boolean.TRUE);
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

}
