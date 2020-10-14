package com.gitee.linzl.bean.cycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class BeanLifeCycle2 implements InitializingBean, DisposableBean {
    public BeanLifeCycle2() {
        System.out.println("dog constructor ……");
    }

    /**
     * 容器销毁
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("dog destroy ……");
    }

    /**
     * 容器初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("dog afterPropertiesSet ……");
    }
}
