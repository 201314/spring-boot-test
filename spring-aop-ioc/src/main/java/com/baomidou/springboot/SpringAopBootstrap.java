package com.baomidou.springboot;

import com.gitee.linzl.EnableAutoCommons;
import com.gitee.linzl.commons.filter.gzip.GzipFilter;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@EnableAutoCommons
// 继承SpringBootServletInitializer，则可打包成war部署在外置tomcat下
// 否则只能通过 java -jar 命令启动
public class SpringAopBootstrap { // extends SpringBootServletInitializer {
    // @Override
    // protected SpringApplicationBuilder configure(SpringApplicationBuilder
    // builder) {
    // builder.bannerMode(Banner.Mode.OFF);
    // return builder.sources(SpringAopBootstrap.class);
    // }
    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(SpringAopBootstrap.class);
        app.bannerMode(Banner.Mode.OFF);
        app.properties(new String[]{});
        ConfigurableApplicationContext context = app.run(args);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName + "==>" + context.getBean(beanDefinitionName));
        }
    }

    @Bean
    public CustomizableTraceInterceptor customizableTraceInterceptor() {
        return new CustomizableTraceInterceptor();
    }

    @Bean
    public Advisor jpaRepositoryAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // pointcut.setExpression("execution(public *
        // org.springframework.data.jpa.repository.JpaRepository+.*(..))");
        pointcut.setExpression("execution(* com.baomidou.springboot.controller.*Rest.getUser())");
        return new DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor());
    }

    @Bean
    public GzipFilter gzipFilter() {
        return new GzipFilter();
    }

    @Bean
    public FilterRegistrationBean<GzipFilter> filterRegistrationBean() {
        FilterRegistrationBean<GzipFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(gzipFilter());
        registration.addUrlPatterns("/*");
        registration.setName("GzipFilter");
        return registration;
    }
}
