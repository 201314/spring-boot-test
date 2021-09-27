package com.gitee.linzl.log.mask.logback;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * depth：正则匹配深度，默认为12，即匹配成功次数达到此值以后终止匹配，主要考虑是性能。如果一个超长的日志，我们不应该全部替换，否则可能引入性能问题。
 * maxLength：单条message的最大长度（不计算throwable），超长则截取，并在message尾部追加终止符。
 * <p>
 * 考虑到扩展性，用户仍然可以直接配置pattern，此时regex、policy、depth等option则不生效。但是maxLength会一致生效。
 * 格式样例：
 * %d{yyyy-MM-dd/HH:mm:ss.SSS}|IP_OR_HOSTNAME|REQUEST_ID|REQUEST_SEQ|^_^|
 * SYS_K1:%property{SYS_K1}|SYS_K2:%property{SYS_K2}|MDC_K1:%X{MDC_K1:--}|MDC_K2:%X{MDC_K2:--}|^_^|
 * [%t] %-5level %logger{50} %line - %m{o1,o2,o3,o4}%n
 * 格式中domain1是必选，而且限定无法扩展
 * domain2根据配置文件指定的system properties和mdcKeys动态拼接，K-V结构，便于解析；可以为空。
 * domain3是常规message部分，其中%m携带options，此后Converter可以获取这些参数。
 **/
@Setter
@Getter
public class CommonPatternLayoutEncoder extends PatternLayoutEncoder {
    protected static final String PATTERN_D1 = "%d{yyyy-MM-dd HH:mm:ss.SSS}|%-5level|%contextName|%thread|%h";
    //protected static final String PATTERN_D2_S1 = "{0}:%property'{'{1}'}'";
    protected static final String PATTERN_D2_S1 = "{0}:{1}";
    /**
     * 如果不需要使用 MessageFormat 格式化时,{}要带上''号
     * MDC 使用时，可填写默认值，具体在以下类中解析
     * ch.qos.logback.core.util.OptionHelper#extractDefaultReplacement(java.lang.String)
     */
    protected static final String PATTERN_D2_S2 = "{0}:%X'{'{1}:-'}'";
    protected static final String PATTERN_D3_S1 = "%logger{50} %line";
    /**
     * 0:message最大长度（超出则截取），1:policy，2:查找深度（超过深度后停止正则匹配）
     */
    protected static final String PATTERN_D3_S2 = "%msg'{'{0},{1},{2}'}'%n";
    /**
     * 来自MDC的key,多个key用逗号分隔。
     */
    protected String mdcKeys = "traceId";
    /**
     * 单条消息的最大长度，主要是message
     */
    protected int maxLength = 5000;
    /**
     * 如果匹配成功，字符串的策略。
     */
    protected String policy = PolicyEnum.REPLACE.name();
    protected int depth = 12;

    protected String systemProperties;

    protected static final String DEFAULT_SYSTEM_PROPERTIES = "profiles";

    private final String FIELD_DELIMITER = "|";
    private final String DOMAIN_DELIMITER = "@";


    @Override
    public void start() {
        if (Objects.nonNull(getPattern())) {
            super.start();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(PATTERN_D1);
        sb.append(FIELD_DELIMITER);
        /**
         * 拼装系统参数，如果当前数据视图不存在，则先set一个默认值
         */
        if (StringUtils.isBlank(systemProperties)) {
            systemProperties = DEFAULT_SYSTEM_PROPERTIES;
        }
        String[] properties = systemProperties.split(",");
        for (String property : properties) {
            sb.append(MessageFormat.format(PATTERN_D2_S1, property, getContext().getObject(property)));
            sb.append(FIELD_DELIMITER);
        }

        // ====  MDC 参数 === START
        sb.append(DOMAIN_DELIMITER);
        //拼接MDC参数
        if (mdcKeys != null) {
            String[] keys = mdcKeys.split(",");
            for (String key : keys) {
                sb.append(MessageFormat.format(PATTERN_D2_S2, key, key));
                sb.append(FIELD_DELIMITER);
            }
            // 去掉最后一个 |
            sb.deleteCharAt(sb.length() - 1);
        } else {
            // 默认打印所有MDC参数
            sb.append("%X");
        }
        sb.append(DOMAIN_DELIMITER);
        // ====  MDC 参数 === END

        sb.append(FIELD_DELIMITER);
        sb.append(PATTERN_D3_S1);
        sb.append(FIELD_DELIMITER);

        if (maxLength < 0 || maxLength > 5000) {
            maxLength = 5000;
        }

        sb.append(MessageFormat.format(PATTERN_D3_S2, String.valueOf(maxLength), policy, String.valueOf(depth)));
        setPattern(sb.toString());
        setCharset(StandardCharsets.UTF_8);
        super.start();
    }
}
