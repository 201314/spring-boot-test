package com.gitee.linzl;

import com.gitee.linzl.commons.annotation.IgnoreScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.lang.annotation.*;

/**
 * 导入spring-common
 * <p>
 * 指定包名的方式扫描存在的一个隐患，若包被重名了，会导致扫描会失效，一般情况下面我们使用
 * basePackageClasses的方式来指定需要扫描的包，这个参数可以指定一些类型，默认会扫描这些类所
 * 在的包及其子包中所有的类，这种方式可以有效避免这种问题
 *
 * @author linzhenlie
 * @date 2019/8/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = IgnoreScan.class)})
public @interface SpringCommons {

}