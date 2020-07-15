package com.gitee.linzl;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuguanqing
 * created 2018/6/22 下午8:01
 * <p>
 * 日志格式转换器，会为每个appender创建一个实例，所以在配置层面需要考虑兼容。
 * 主要目的是，根据配置的regex来匹配message，对于匹配成功的字符串进行替换操作，并返回修正后的message。
 **/
@Slf4j
public class ComplexMessageConverter extends MessageConverter {
    protected String regex = "-";
    protected int depth = 0;
    protected String policy = "-";
    protected int maxLength = 2048;
    private ReplaceMatcher replaceMatcher = new ReplaceMatcher();

    @Override
    public void start() {
        List<String> options = getOptionList();
        //如果存在参数选项，则提取
        if (options != null && options.size() == 4) {
            maxLength = Integer.valueOf(options.get(0));
            regex = options.get(1);
            policy = options.get(2);
            depth = Integer.valueOf(options.get(3));

            if ((regex != null && !regex.equals("-"))
                    //&& (PolicyEnum.codeOf(policy) != null)
                    && depth > 0) {
                replaceMatcher = new ReplaceMatcher();
            }
        }
        super.start();
    }

    @Override
    public String convert(ILoggingEvent event) {
        String source = event.getFormattedMessage();
        Object[] obj = event.getArgumentArray();
        if(obj[0].getClass().isAssignableFrom(Student.class)){
            log.info("我是学生0");
        }
        if(obj[1].getClass().isAssignableFrom(Student.class)){
            log.info("我是学生1");
        }
        if (source == null || source.isEmpty()) {
            return source;
        }
        //复杂处理的原因：尽量少的字符串转换、空间重建、字符移动。共享一个builder
        if (source.length() > maxLength || Objects.nonNull(replaceMatcher)) {
            StringBuilder sb = null;
            //如果超长截取
            if (source.length() > maxLength) {
                sb = new StringBuilder(maxLength + 6);
                sb.append(source.substring(0, maxLength))
                        //后面增加三个终止符
                        .append("❮❮❮");
            }
            //如果启动了matcher
            if (Objects.nonNull(replaceMatcher)) {
                //如果没有超过maxLength
                if (Objects.isNull(sb)) {
                    sb = new StringBuilder(source);
                }
                return replaceMatcher.execute(sb, policy);
            }
            return sb.toString();
        }
        return source;
    }

    class ReplaceMatcher {
        Pattern pattern;

        ReplaceMatcher() {
            pattern = Pattern.compile(regex);
        }

        String execute(StringBuilder source, String policy) {
            Matcher matcher = pattern.matcher(source);

            int i = 0;
            while (matcher.find() && (i < depth)) {
                i++;
                int start = matcher.start();
                int end = matcher.end();
                if (start < 0 || end < 0) {
                    break;
                }
                String group = matcher.group();
                switch (policy) {
                    case "drop":
                        return "❯❮";//只要匹配，立即返回
                    case "replace":
                        source.replace(start, end, facade(group, true));
                        break;
                    case "erase":
                    default:
                        source.replace(start, end, facade(group, false));
                        break;
                }
            }
            return source.toString();
        }
    }

    /**
     * 混淆，但是不能改变字符串的长度
     *
     * @param source
     * @param included
     * @return
     */
    public static String facade(String source, final boolean included) {
        int length = source.length();
        StringBuilder sb = new StringBuilder();
        /**
         *长度超过11的，保留前三、后四，中间全部*替换
         *低于11位或者included=false，全部*替换
         */
        if (length >= 11) {
            if (included) {
                sb.append(source.substring(0, 3));
            } else {
                sb.append("***");
            }
            sb.append(repeat('*', length - 7));
            if (included) {
                sb.append(source.substring(length - 4));
            } else {
                sb.append(repeat('*', 4));
            }
        } else {
            sb.append(repeat('*', length));
        }

        return sb.toString();
    }

    private static String repeat(char t, int times) {
        char[] r = new char[times];
        for (int i = 0; i < times; i++) {
            r[i] = t;
        }
        return new String(r);
    }
}
