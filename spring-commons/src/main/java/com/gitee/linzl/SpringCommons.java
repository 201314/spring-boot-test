package com.gitee.linzl;

import com.gitee.linzl.commons.annotation.IgnoreScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.lang.annotation.*;

/**
 * 导入spring-common
 *
 * @author linzhenlie
 * @date 2019/8/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
/**
 * SpringBootApplication 可以排除自动启动(AutoConfiguration)的类
 *
 * ComponentScan 可以排除自定义的扫描类
 */
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = IgnoreScan.class)})

public @interface SpringCommons {

}
