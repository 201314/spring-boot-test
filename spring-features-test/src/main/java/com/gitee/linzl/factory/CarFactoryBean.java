package com.gitee.linzl.factory;

import com.gitee.linzl.domain.CarDomain;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class CarFactoryBean implements FactoryBean<CarDomain> {
    private String carInfo = "大众SUV,180,180000";

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