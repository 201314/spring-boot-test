package com.gitee.linzl.commons.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson支持LocalDateTime,LocalDate返回时间戳，默认时间返回给前端统一为时间戳
 *
 * @author linzhenlie-jk
 * @date 2020/7/17
 * @see org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
 */
@JsonComponent
@Slf4j
public class JacksonConfig {
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
            builder.timeZone(TimeZone.getTimeZone("GMT+8"));
        };
    }
}
