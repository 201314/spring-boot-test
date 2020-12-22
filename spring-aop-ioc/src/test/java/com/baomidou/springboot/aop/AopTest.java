package com.baomidou.springboot.aop;

import com.baomidou.springboot.services.IMyService;
import com.baomidou.springboot.services.MyService;
import com.gitee.linzl.commons.api.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AopTest {
    /**
     * 需要注意的是在使用AspectjProxyFactory基于切面类创建代理对象时，
     * <p>
     * 我们指定的切面类上必须包含@Aspect注解。
     * <p>
     * 另外需要注意的是虽然我们自己通过编程的方式可以通过AspectjProxyFactory
     * <p>
     * 创建基于@Aspect标注的切面类的代理，但是通过配置<aop:aspectj-autoproxy/>
     * 使用基于注解的Aspectj风格的Aop时，Spring内部不是通过AspectjProxyFactory创建的代理对象，
     * <p>
     * 而是通过ProxyFactory。有兴趣的朋友可以查看一下AnnotationAwareAspectjAutoProxyCreator的源代码。
     */
    @Test
    public void testAop() {
        IMyService myService = new MyService();
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(myService);
        proxyFactory.addAspect(MyAspect.class);
        // 此处最好设置为true,根据需要使用CGLIB或JDK动态代理
        proxyFactory.setProxyTargetClass(true);
        IMyService proxy = proxyFactory.getProxy();
        proxy.add();
    }

    @Test
    public void testMethodInterceptor() {
        MyService myService = new MyService();
        ProxyFactory proxyFactory = new ProxyFactory(myService);
        proxyFactory.addAdvice(new MyMethodInterceptor());
        // 此处最好设置为true,根据需要使用CGLIB或JDK动态代理
        proxyFactory.setProxyTargetClass(true);
        MyService proxy = (MyService) proxyFactory.getProxy();
        proxy.doSomeThing("通过代理工厂设置代理对象，拦截代理方法");
    }

    @Autowired
    private IMyService myService;

    @Test
    public void testExceptionCatch2() {
        ApiResult apiResult = myService.doSomeThing2("哈哈");
        log.info("apiResult==" + apiResult);
    }

    @Test
    public void testExceptionCatch3() {
        myService.doSomeThing3("没有返回结果");
    }

    @Test
    public void testExceptionCatch4() {
        myService.doSomeThing4("没有返回结果");
    }

    @Test
    public void testExceptionCatch5() {
        ApiResult apiResult = myService.doSomeThing5("有返回结果，但是运行时异常");
        log.info("apiResult==" + apiResult);
    }
}
