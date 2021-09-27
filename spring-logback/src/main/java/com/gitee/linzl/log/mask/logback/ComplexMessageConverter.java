package com.gitee.linzl.log.mask.logback;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.gitee.linzl.log.core.utils.DesensitizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 日志格式转换器，最大长度，约定脱敏
 **/
@Slf4j
public class ComplexMessageConverter extends MessageConverter {
    protected int maxLength = 5000;

    @Override
    public String convert(ILoggingEvent event) {
        String source = event.getFormattedMessage();
        if (StringUtils.isBlank(source)) {
            return source;
        }

        if (Objects.isNull(event.getArgumentArray())) {
            return source;
        }
        // 获取所有参数信息，进行正则匹配，看是否为需要脱敏的数据项
        StringBuilder str = new StringBuilder();
        Object[] arr = event.getArgumentArray();
        for (Object o : arr) {
            if (o == null || o instanceof String) {
                str.append((String) o);
            }
        }

        DesensitizationUtil util = new DesensitizationUtil();
        if (source.length() <= maxLength) {
            String changeMessage = util.customChange(source);
            return changeMessage;
        }

        //复杂处理的原因：尽量少的字符串转换、空间重建、字符移动。共享一个builder
        StringBuilder sb = null;
        //如果超长截取
        if (source.length() > maxLength) {
            sb = new StringBuilder(maxLength + 6);
            sb.append(source.substring(0, maxLength))
                    //后面增加三个终止符
                    .append("❮❮❮");
        }

        // 获取替换后的日志信息
        String changeMessage = util.customChange(source);
        return changeMessage;
    }
}
