package com.gitee.linzl;

import org.springframework.context.annotation.Import;

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
@Import(SpringCommons.class)
public @interface EnableAutoCommons {

}
