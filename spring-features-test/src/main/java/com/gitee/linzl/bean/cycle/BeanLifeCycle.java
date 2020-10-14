package com.gitee.linzl.bean.cycle;

import com.gitee.linzl.domain.CarDomain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * bean的生命周期：
 *      bean的创建（bean调用） - 初始化（initMethod） - 销毁（destroyMethod） 的过程
 *
 * 容器管理bean的生命周期：
 *      我们可以自定义初始化的销毁方法，容器在bean进行到当前生命周期的时候，来调用我们自定义的初始化和销毁方法
 *
 * new 构造方法（对象创建）
 *      单实例singleton：在容器启动（new AnnotationConfigApplicationContext）的时候创建对象
 *      多实例prototype：在每次获取的时候（applicationContext.getBean）创建对象
 *
 * 初始化：
 *      当“对象创建完成并赋值好（new Car）”，调用初始化方法（initMethod）……
 * 销毁：
 *      单实例：容器关闭的时候（applicationContext.close()）进行销毁
 *      多实例：容器不会管理这个bean；容器不会调用销毁方法（applicationContext.close()）
 *
 * 1）、指定初始化和销毁方法
 *      通过指定init-method 和 destroy-method
 * 2）、通过让Bean实现InitializingBean（定义初始化逻辑）
 *                  DisposableBean（定义销毁逻辑）
 * </pre>
 **/
@Configuration
@ComponentScan
public class BeanLifeCycle {

    // @Scope("prototype")
    @Bean(initMethod = "init", destroyMethod = "destroy")
    public CarDomain car() {
        return new CarDomain();
    }
}
