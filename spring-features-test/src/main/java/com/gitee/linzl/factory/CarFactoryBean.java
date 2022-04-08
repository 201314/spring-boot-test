package com.gitee.linzl.factory;

import com.gitee.linzl.domain.CarDomain;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * 自定义（定制化）实例化bean的逻辑
 * <bean id="car" class="com.gitee.linzl.factory.CarFactoryBean" carInfo="超级跑车，400，2000000"/>
 * <p>
 * 最常规的方式
 * <bean id="car" class="com.gitee.linzl.domain.CarDomain">
 *      <property name ="brand" value="超级跑车"/>
 *      <property name ="maxSpeed" value="400"/>
 *      <property name ="price" value="2000000"/>
 * </bean>
 */
@Component
public class CarFactoryBean implements FactoryBean<CarDomain> {
    private String carInfo = "超级跑车，400，2000000";

    @Override
    public CarDomain getObject() throws Exception {
        CarDomain car = new CarDomain();
        String[] infos = carInfo.split(",");
        car.setBrand(infos[0]);
        car.setMaxSpeed(Integer.valueOf(infos[1]));
        car.setPrice(Double.valueOf(infos[2]));
        return car;
    }

    @Override
    public Class<CarDomain> getObjectType() {
        return CarDomain.class;
    }

    /**
     * 是否单例？
     * true：这个bean是单实例，在容器中保存一份
     * false：多实例，每次获取都会创建一个新的bean
     *
     * @return
     */
    @Override
    public boolean isSingleton() {
        return false;
    }
}