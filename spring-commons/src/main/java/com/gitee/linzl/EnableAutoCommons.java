package com.gitee.linzl;

import com.gitee.linzl.commons.annotation.IgnoreScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.lang.annotation.*;

/**
 * 在springboot的业务启动类中使用该注解引入
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import(SpringCommons.class)
@SpringBootApplication
@ComponentScan(
        basePackageClasses = SpringCommons.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = IgnoreScan.class)})
public @interface EnableAutoCommons {

}
