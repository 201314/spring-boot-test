package com.gitee.linzl.commons.tools;

import org.junit.jupiter.api.Test;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author linzhenlie-jk
 * @date 2021/1/18
 */
public class PropertyPlaceholderHelperTest {
    private final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

    @Test
    void withProperties() {
        String text = "foo=${foo},ssss=${hello}";
        Properties props = new Properties();
        props.setProperty("foo", "bar");
        props.setProperty("hello", "b111ar");

        System.out.println(this.helper.replacePlaceholders(text, props));
    }
}
