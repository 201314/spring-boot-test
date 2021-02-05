package com.gitee.linzl.commons.tools;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 一定要在MvcConfig中初始这个工具类的bean
 * <p>
 * 根据bean名称，获取对应的bean实例
 *
 * @author linzl
 * <p>
 * 2016年11月11日
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据beanName 获取bean实例
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据类型进行获取Bean的实例
     *
     * @param clsType
     * @return
     */
    public static <T> T getBean(Class<T> clsType) {
        return applicationContext.getBean(clsType);
    }

    /**
     * 根据bean名称和类型进行获取Bean的实例
     *
     * @param beanName
     * @param clsType
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> clsType) {
        return applicationContext.getBean(beanName, clsType);
    }

    public static <T> Map<String, T> getBeansMap(Class<T> clsType) throws NoSuchBeanDefinitionException {
        return applicationContext.getBeansOfType(clsType);
    }
    public static <T> List<T> getBeansList(Class<T> clsType) throws NoSuchBeanDefinitionException {
        return applicationContext.getBeanProvider(clsType).stream().collect(Collectors.toList());
    }
    /**
     * 判断是否包含此bean，有则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        if (Objects.nonNull(applicationContext)) {
            return applicationContext.containsBean(name);
        }
        return false;
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }
}
