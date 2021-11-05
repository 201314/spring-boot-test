package com.gitee.linzl.commons.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * 表单入参，字符串时间转换
 *
 * @author linzhenlie-jk
 * @date 2020/10/16
 */
public class FormStringToDateConverter {
    public final static String[] patterns;

    public final static String defaultPatttern = "yyyy-MM-dd HH:mm:ss";

    public final static String formatter_pattern_23 = "yyyy-MM-dd HH:mm:ss.SSS";

    public final static String formatter_dt19_pattern_tw = "yyyy/MM/dd HH:mm:ss";

    public final static String formatter_dt19_pattern_cn = "yyyy年M月d日 HH:mm:ss";

    public final static String formatter_dt19_pattern_cn_1 = "yyyy年M月d日 H时m分s秒";

    public final static String formatter_dt19_pattern_us = "MM/dd/yyyy HH:mm:ss";

    public final static String formatter_dt19_pattern_eur = "dd/MM/yyyy HH:mm:ss";

    public final static String formatter_dt19_pattern_de = "dd.MM.yyyy HH:mm:ss";

    public final static String formatter_dt19_pattern_in = "dd-MM-yyyy HH:mm:ss";

    public final static String formatter_d8_pattern = "yyyyMMdd";

    public final static String formatter_d10_pattern = "yyyy-MM-dd";

    public final static String formatter_d10_pattern_tw = "yyyy/MM/dd";

    public final static String formatter_d10_pattern_cn = "yyyy年M月d日";

    public final static String formatter_d10_pattern_us = "MM/dd/yyyy";

    public final static String formatter_d10_pattern_eur = "dd/MM/yyyy";

    public final static String formatter_d10_pattern_de = "dd.MM.yyyy";

    public final static String formatter_d10_pattern_in = "dd-MM-yyyy";

    public final static String formatter_iso8601_pattern = "yyyy-MM-dd'T'HH:mm:ss";

    public final static String formatter_iso8601_pattern_23 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public final static String formatter_iso8601_pattern_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS";

    static {
        List<String> patternLists = new ArrayList<>();
        patternLists.add(defaultPatttern);
        patternLists.add(formatter_pattern_23);
        patternLists.add(formatter_dt19_pattern_tw);
        patternLists.add(formatter_dt19_pattern_cn);
        patternLists.add(formatter_dt19_pattern_cn_1);
        patternLists.add(formatter_dt19_pattern_us);
        patternLists.add(formatter_dt19_pattern_eur);
        patternLists.add(formatter_dt19_pattern_de);
        patternLists.add(formatter_dt19_pattern_in);

        patternLists.add(formatter_d8_pattern);
        patternLists.add(formatter_d10_pattern);
        patternLists.add(formatter_d10_pattern_tw);
        patternLists.add(formatter_d10_pattern_cn);
        patternLists.add(formatter_d10_pattern_us);
        patternLists.add(formatter_d10_pattern_eur);
        patternLists.add(formatter_d10_pattern_de);
        patternLists.add(formatter_d10_pattern_in);

        patternLists.add(formatter_iso8601_pattern);
        patternLists.add(formatter_iso8601_pattern_23);
        patternLists.add(formatter_iso8601_pattern_29);

        patterns = patternLists.toArray(new String[0]);
    }

    /**
     * Long 转 Date
     *
     * @see org.springframework.format.datetime.DateFormatterRegistrar.LongToDateConverter
     */
    @ReadingConverter
    public static class StringToDateConverter implements Converter<String, Date> {
        @SneakyThrows
        @Override
        public Date convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            return DateUtils.parseDate(source.trim(), patterns);
        }
    }

    @ReadingConverter
    public static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        @SneakyThrows
        @Override
        public LocalDate convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            Date date = DateUtils.parseDate(source.trim(), patterns);
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDate();
        }
    }

    @ReadingConverter
    public static class LongToLocalDateConverter implements Converter<Long, LocalDate> {
        @Override
        public LocalDate convert(Long milliseconds) {
            if (Objects.isNull(milliseconds)) {
                return null;
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault()).toLocalDate();
        }
    }

    @ReadingConverter
    public static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @SneakyThrows
        @Override
        public LocalDateTime convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            Date date = DateUtils.parseDate(source.trim(), patterns);
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
    }

    @ReadingConverter
    public static class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
        @SneakyThrows
        @Override
        public LocalDateTime convert(Long milliseconds) {
            if (Objects.isNull(milliseconds)) {
                return null;
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
        }
    }
}