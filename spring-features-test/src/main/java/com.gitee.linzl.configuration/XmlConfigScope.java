package com.gitee.linzl.configuration;

import com.gitee.linzl.condition.LinuxCondition;
import com.gitee.linzl.condition.WindowsCondition;
import com.gitee.linzl.domain.CarDomain;
import com.gitee.linzl.imports.CustomImport;
import com.gitee.linzl.imports.CustomImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.*;

/**
 * 使用注解的方式替代xml的方式进行配置
 */
//可以加入到类上，满足当前条件，这个类中配置的所有bean注册才能生效
// @Conditional({WindowsCondition.class})

/**
 * <pre>
 * 给容器中注册组件：
 * 1）：包扫描 + 组件标注注解（@COntroller、@Service、@Repository、@Component等）【自己写的类】
 * 2）：@Bean 【导入的第三方包里面的组件】
 * 3）：@Import【快速给容器中导入一个组件】
 *      1）、@Import（要导入到容器中的主键）：容器中就会自动注册这个主键，id默认是全类名
 *      2）、ImportSelector：返回需要导入的组件的全类名数组
 *      3）、ImportBeanDefinitionRegistrar：手动注册bean容器中
 * 4）：使用Spring提供的FactoryBean（工厂Bean）
 * id 默认是组件的全类名
 * com.shunxi.bean.Red
 * com.shunxi.bean.Yellow
 * </pre>
 */
@Import({CustomImport.class, CustomImportBeanDefinitionRegistrar.class})
@Configuration
public class XmlConfigScope {

    /**
     * <pre>
     * @Bean
     *      给容器中注入一个Bean，
     *      默认：类型为返回值的类型，id为方法名称
     *      可以更改方法id的名称，在Bean指定value
     *
     * @Scope
     *      singleton：单实例的（默认值）：IOC容器启动会调用方法创建对象放到IOC容器中
     *                                    以后每次获取就是直接从容器（map.get()）中拿。
     *      prototype：多实例的；IOC容器启动并不会去调用方法创建对象放到容器中。
     *                          每次获取的时候才会调用方法创建对象。
     *      request：同一次请求创建一个实例
     *      session：同一个session创建一个实例
     *
     * @Lazy
     *      懒加载，1：针对单实例bean；
     *              2：未配置懒加载，默认在容器启动的时候创建对象；
     *              3：单实例配置了懒加载，容器启动不创建对象，第一次使用（获取）Bean创建对象，并初始化
     * </pre>
     *
     * @return
     */
    @Lazy
    @Scope(value = "singleton")  // 可以不写，默认不写就是懒加载
//    @Scope(value = "prototype")
    @Bean(value = "person")
    public CarDomain person() {
        return new CarDomain("person");
    }

    /**
     * <pre>
     * @Conditional({Condition})，按照一定的条件进行判断，满足条件给容器中注册bean
     *      如果系统是windows，给容器中注入（"personWindows"）
     *      如果系统是linux，给容器中注入（"personLinux"）
     *      使用自定义条件（windows）选择IOC是否加载
     * </pre>
     *
     * @return
     */
    @Conditional(WindowsCondition.class)
    @Bean(value = "personWindows")
    public CarDomain personWindows() {
        return new CarDomain("windows");
    }

    /**
     * 使用自定义条件（linux）选择IOC是否加载
     *
     * @return
     */
    @Conditional(LinuxCondition.class)
    @Bean(value = "personLinux")
    public CarDomain personLinux() {
        return new CarDomain("linux");
    }

    /**
     * 工厂bean
     *
     * @return
     */
    @Bean("customColorFactoryBean")
    public CarFactoryBean getCustomColorFactoryBean() {
        return new CarFactoryBean();
    }
}
