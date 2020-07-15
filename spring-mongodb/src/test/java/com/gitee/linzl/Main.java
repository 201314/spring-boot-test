package com.gitee.linzl;

import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends Generic<String> {
    private List<Map<String, List<Object>>> list = new ArrayList<>();

    public static void main(String[] args) throws NoSuchFieldException {
        // 具体参数类型
        Main testClass = new Main();
        System.out.println(getGenericType(testClass.getClass()));

        // 获取成员泛型参数类型
        Field field = Main.class.getDeclaredField("list");
        ResolvableType resolvableType = ResolvableType.forField(field);
        System.out.println(resolvableType.getGeneric(0));
    }

    /**
     * 获取所继承类的泛型实际类型
     *
     * @param declaredClass 传入Class实例
     * @return 泛型实际类型
     */
    private static Class getGenericType(Class declaredClass) {
        ParameterizedType parameterizedType = (ParameterizedType) declaredClass.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class) actualTypeArguments[0];
    }
}