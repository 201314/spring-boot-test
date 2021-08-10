package com.baomidou.springboot;

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
import org.springframework.transaction.annotation.EnableTransactionManagement;
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

    @Profile("mybatisSessionClose")
    @Bean
    public List<ConfigurationCustomizer> list() {
        log.info("Session一级缓存【关闭】");
        List<ConfigurationCustomizer> configurationCustomizers = new ArrayList<>();
        configurationCustomizers.add(new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                // Session一级缓存【关闭】
                configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
            }
        });
        return configurationCustomizers;
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

    @Profile("trxRequired")
    @Bean
    public TransactionInterceptor trxRequired() {
        log.info("配置事务配置trxRequired");
        Map<String, TransactionAttribute> txMap = new HashMap<>(5);

        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txMap.put("*Trx", requiredTx);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(source);
        return interceptor;
    }

    @Profile("trxAndSupport")
    @Bean
    public TransactionInterceptor trxAndSupport() {
        log.info("配置事务配置trxAndSupport");
        Map<String, TransactionAttribute> txMap = new HashMap<>(5);

        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setReadOnly(false);
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
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

   /*@Bean
    public Advisor testIn() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 拦截所有
        pointcut.setExpression("execution(* com.baomidou.springboot.services.*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, trxAndSupport());
    }*/

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
