package com.baomidou.springboot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.gitee.linzl.EnableAutoCommons;
import com.gitee.linzl.commons.filter.gzip.GzipFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAutoCommons
@Slf4j
/**
 *  继承SpringBootServletInitializer，则可打包成war部署在外置tomcat下,否则只能通过 java -jar 命令启动
 */
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
        app.profiles("trxRequired");
        ConfigurableApplicationContext context = app.run(args);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName + "==>" + context.getBean(beanDefinitionName));
        }
    }

    // -----  对Controller getUser方法进行跟踪 START
    @Bean
    public Advisor jpaRepositoryAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.baomidou.springboot.controller.*Rest.getUser())");
        return new DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor());
    }

    @Bean
    public CustomizableTraceInterceptor customizableTraceInterceptor() {
        return new CustomizableTraceInterceptor();
    }
    // -----  对Controller getUser方法进行跟踪 END

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

    // ============= 事务配置 START

    /**
     * 切面拦截规则
     */
    @Bean
    public Advisor advisor(@Autowired @Qualifier("transactionInterceptorExt") TransactionInterceptor transactionInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 拦截所有service服务方法
        pointcut.setExpression("execution(* com.baomidou.springboot.services.*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, transactionInterceptor);
    }

    /**
     * 事务拦截类型
     */
    @Profile("trxRequired")
    @Bean("transactionInterceptorExt")
    public TransactionInterceptor trxRequired(PlatformTransactionManager transactionManager) {
        log.info("配置事务配置trxRequired");
        Map<String, TransactionAttribute> txMap = new HashMap<>(5);

        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx =
                new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
                        Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setReadOnly(false);
        txMap.put("*Trx", requiredTx);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(source);
        return interceptor;
    }

    @Profile("trxAndSupport")
    @Bean("transactionInterceptorExt")
    public TransactionInterceptor trxAndSupport(PlatformTransactionManager transactionManager) {
        log.info("配置事务配置trxAndSupport");
        Map<String, TransactionAttribute> txMap = new HashMap<>(5);

        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx =
                new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
                        Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setReadOnly(false);
        txMap.put("*Trx", requiredTx);

        RuleBasedTransactionAttribute supportTrx = new RuleBasedTransactionAttribute();
        supportTrx.setReadOnly(false);
        supportTrx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        txMap.put("*", supportTrx);

        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(source);
        return interceptor;
    }
    // ============= 事务配置 END
}
