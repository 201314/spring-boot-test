package com.gitee.linzl;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.util.ContextUtil;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author liuguanqing
 * created 2018/6/22 下午8:01
 * 适用于基于File的Appender
 * <p>
 * 限定我司日志规范，增加有关敏感信息的过滤。
 * 可以通过regex指定需要匹配和过滤的表达式，对于符合表达式的字符串，则采用policy进行处理。
 * 1）replace：替换，将字符串替换为facade，比如：18611001100 > 186****1100
 * 2) drop：抛弃整条日志
 * 3）erase：擦除字符串，全部替换成等长度的"****"，18611001100 > ***********
 * <p>
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
    protected static final String PATTERN_D1 = "%d'{'yyyy-MM-dd HH:mm:ss.SSS'}'|%contextName|%thread|{0}|%X'{'requestId:--'}'|%X'{'requestSeq:--'}'";
    //protected static final String PATTERN_D2_S1 = "{0}:%property'{'{1}'}'";
    protected static final String PATTERN_D2_S1 = "{0}:{1}";
    protected static final String PATTERN_D2_S2 = "{0}:%X'{'{1}:--'}'";
    protected static final String PATTERN_D3_S1 = "%-5level %logger{50} %line|";
    /**
     * 0:message最大长度（超出则截取），1:正则表达式，2:policy，3:查找深度（超过深度后停止正则匹配）
     */
    protected static final String PATTERN_D3_S2 = "%msg'{'{0},{1},{2}'}'%n";
//    protected static final String PATTERN_D3_S2 = "%msg'{'{0},{1},{2},{3}'}'%n";
    /**
     * 来自MDC的key,多个key用逗号分隔。
     */
    protected String mdcKeys;
    /**
     * 默认使用脱敏
     */
    private boolean desensitizeSwitch = true;
    /**
     * 单条消息的最大长度，主要是message
     */
    protected int maxLength = 2048;
    /**
     * 如果匹配成功，字符串的策略。
     */
    protected String policy = "replace";

    protected int depth = 128;

    protected boolean useDefaultRegex = true;
    protected String systemProperties;

    protected static final String DEFAULT_SYSTEM_PROPERTIES = "projectName,profiles,clusterName";

    private String FIELD_DELIMITER = "|";
    private String DOMAIN_DELIMITER = "@";


    @Override
    public void start() {
        if (Objects.nonNull(getPattern())) {
            super.start();
            return;
        }

        StringBuilder sb = new StringBuilder();
        String hostName = null;
        try {
            hostName = ContextUtil.getLocalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String d1 = MessageFormat.format(PATTERN_D1, hostName);
        sb.append(d1);
        sb.append(FIELD_DELIMITER);
        sb.append(DOMAIN_DELIMITER);
        sb.append(FIELD_DELIMITER);
        /**
         * 拼装系统参数，如果当前数据视图不存在，则先set一个默认值
         */
        if (Objects.isNull(systemProperties) || systemProperties.isEmpty()) {
            systemProperties = DEFAULT_SYSTEM_PROPERTIES;
        }
        String[] properties = systemProperties.split(",");
        for (String property : properties) {
            sb.append(MessageFormat.format(PATTERN_D2_S1, property,getContext().getObject(property)));
            sb.append(FIELD_DELIMITER);
        }

        //拼接MDC参数
        if (mdcKeys != null) {
            String[] keys = mdcKeys.split(",");
            for (String key : keys) {
                sb.append(MessageFormat.format(PATTERN_D2_S2, key));
                sb.append(FIELD_DELIMITER);
            }
            sb.append(DOMAIN_DELIMITER);
            sb.append(FIELD_DELIMITER);
        }
        sb.append(PATTERN_D3_S1);

        //if (PolicyEnum.codeOf(policy) == null) {
        //    policy = "-";
        //}

        if (maxLength < 0 || maxLength > 10240) {
            maxLength = 2048;
        }

        sb.append(MessageFormat.format(PATTERN_D3_S2, String.valueOf(maxLength), policy, String.valueOf(depth)));
        setPattern(sb.toString());
        super.start();
    }

    @Override
    public String getPattern() {
        return super.getPattern();
    }

    @Override
    public void setPattern(String pattern) {
        super.setPattern(pattern);
    }

    public static void main(String[] args) {
        int maxLength = 2048;
        String policy = "hhhh";
        String regex = "regex正则";
        int depth = 100;
        System.out.println(MessageFormat.format(PATTERN_D3_S2, String.valueOf(maxLength), regex, policy, String.valueOf(depth)));

        String[] properties = DEFAULT_SYSTEM_PROPERTIES.split(",");
        StringBuffer sb = new StringBuffer();
        for (String property : properties) {
            sb.append(MessageFormat.format(PATTERN_D2_S1, property));
            sb.append("|");
        }
        System.out.println("Sb:"+sb);
    }
}
