package com.gitee.linzl.configuration;

import com.gitee.linzl.domain.CarDomain;
import com.gitee.linzl.filter.CustomFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * 使用注解的方式替代xml的方式进行配置
 *
 * @author YSW
 * @create 2019-12-15 23:21
 **/

/**
 * 告诉spring他是一个注解类
 */
@Configuration

/**
 * 告诉我们spring注解会把@Controller、@Server、@Repository、@Component、@Repository等配置，注入到spring容器中
 * includeFilters：指定扫描的时候只需要包含哪些组件
 * excludeFilters：指定扫描的时候按照什么规则 排除哪些组件
 * useDefaultFilters：禁用掉默认规则 -> 注意：只会在includeFilters中生效
 * FilterType.ANNOTATION：按照注解进行过滤
 * FilterType.ASSIGNABLE_TYPE：按照给定的类型（可以理解为实体类的class）
 * FilterType.ASPECTJ：使用ASPECTJ表达式
 * FilterType.CUSTOM：自定义过滤
 */
//@ComponentScan(value = "com.shunxi", includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
//}, useDefaultFilters = false)
//@ComponentScan(value = "com.shunxi", excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class, Service.class})
//})
//@ComponentScan(value = "com.shunxi", includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE
//                , value = {PersonServiceImpl.class, PersonController.class})
//}, useDefaultFilters = false)
@ComponentScan(value = "com.shunxi",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.CUSTOM, value = {CustomFilter.class})
        },
        useDefaultFilters = false)
public class XmlConfig {

    /**
     * 给容器中注入一个Bean，
     * 默认：类型为返回值的类型，id为方法名称
     * 可以更改方法id的名称，在Bean指定value
     *
     * @return
     */
    @Bean(value = "CarDomainperson")
    public CarDomain person() {
        return new CarDomain("XmlConfig zhangsan");
    }
}
