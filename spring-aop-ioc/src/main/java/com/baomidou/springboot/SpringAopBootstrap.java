package com.baomidou.springboot;

import com.gitee.linzl.EnableAutoCommons;
import com.gitee.linzl.commons.filter.gzip.GzipFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableTransactionManagement
@EnableAutoCommons
@Slf4j
// 继承SpringBootServletInitializer，则可打包成war部署在外置tomcat下
// 否则只能通过 java -jar 命令启动
public class SpringAopBootstrap extends SpringBootServletInitializer {
     @Override
     protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
         builder.bannerMode(Banner.Mode.OFF);
         return builder.sources(SpringAopBootstrap.class);
     }
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

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean  //配置事务
    public TransactionInterceptor test() {
        log.info("配置事务配置事务配置事务配置事务配置事务配置事务配置事务");
        Map<String, TransactionAttribute> txMap = new HashMap<>();

        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txMap.put("*Trx", requiredTx);

        RuleBasedTransactionAttribute supportTrx = new RuleBasedTransactionAttribute();
        supportTrx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        txMap.put("*", supportTrx);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public Advisor testIn() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 拦截所有
        pointcut.setExpression("execution(* com.baomidou.springboot.services.*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, test());
    }

    /**@Bean  配置事务**/
    public TransactionInterceptor txAdvice() {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //requiredTx.setTimeout(TX_METHOD_TIMEOUT);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("*Trx", requiredTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        txMap.put("select*", readOnlyTx);
        source.setNameMap(txMap);
        return new TransactionInterceptor(transactionManager, source);
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
