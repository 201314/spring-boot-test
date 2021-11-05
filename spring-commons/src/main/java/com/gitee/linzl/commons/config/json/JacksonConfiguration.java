package com.gitee.linzl.commons.config.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

/**
 * Jackson支持LocalDateTime,LocalDate返回时间戳，默认时间返回给前端统一为时间戳
 *
 * @author linzhenlie-jk
 * @date 2020/7/17
 * @see org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
 */
@JsonComponent
@Slf4j
public class JacksonConfiguration {
    /**
     * 日期序列化
     * <p>
     * LocalDateSerializer \ LocalDateTimeSerializer 默认返回的是String
     */
    public static class LocalDateTimeToJackSonLongSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    public static class LocalDateToJackSonLongSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return builder -> {
            //若POJO对象的属性值为null，序列化时不进行显示
            //builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            // 忽略 transient 修饰的属性
            builder.featuresToEnable(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
            builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.featuresToEnable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
            // POJO对象必须要有字段，否则Jackson报错FAIL_ON_EMPTY_BEANS
            builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // {"list": ["2021-11-11 11:11:11.333"]} 允许转为时间
            builder.featuresToEnable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
            builder.timeZone(TimeZone.getTimeZone("GMT+8"));

            // 字符串序列化为时间,允许多种格式的时间字符串 , 也可以像上面两个写成内部类
            builder.deserializerByType(LocalDate.class, LocalDateDeserializerExt.INSTANCE);
            builder.deserializerByType(LocalDateTime.class, LocalDateTimeDeserializerExt.INSTANCE);
        };
    }
}
