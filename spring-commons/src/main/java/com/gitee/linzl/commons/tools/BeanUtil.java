package com.gitee.linzl.commons.tools;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Slf4j
public class BeanUtil {
    /**
     * 把source复制到target,复制相同属性名称,注意属性类型也要一致
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 把source复制到target,复制相同属性名称,注意属性类型也要一致,忽略ignoreProperties的复制
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 把集合A复制到集合B中去,复制相同属性名称,注意属性类型也要一致
     *
     * @param source
     * @param target 集合B存储的类型
     * @return
     */
    public static <T> List<T> copyProperties(Collection<?> source, Class<T> target) {
        return copyProperties(source, target, (String[]) null);
    }

    /**
     * 把集合A复制到集合B中去,复制相同属性名称,注意属性类型也要一致,忽略ignoreProperties的复制
     *
     * @param source
     * @param target           集合B存储的类型
     * @param ignoreProperties 忽略ignoreProperties的复制
     * @return
     */
    public static <T> List<T> copyProperties(Collection<?> source, Class<T> target, String... ignoreProperties) {
        List<T> newList = new ArrayList<T>();
        for (Object object : source) {
            T obj = (T) BeanUtils.instantiateClass(target);
            BeanUtils.copyProperties(object, obj, ignoreProperties);
            newList.add(obj);
        }
        return newList;
    }

    /**
     * 把集合A复制到集合B中去,复制相同属性名称,注意属性类型也要一致
     *
     * @param source
     * @param target 集合B存储的类型
     * @return
     */
    public static <T> Page<T> copyProperties(Page<?> source, Class<T> target) {
        return copyProperties(source, target, (String[]) null);
    }

    /**
     * 把集合A复制到集合B中去,复制相同属性名称,注意属性类型也要一致
     *
     * @param source
     * @param target           集合B存储的类型
     * @param ignoreProperties 忽略ignoreProperties的复制
     * @return
     */
    public static <T> Page<T> copyProperties(Page<?> source, Class<T> target, String... ignoreProperties) {
        List<T> newList = copyProperties(source.getContent(), target, ignoreProperties);
        PageRequest pageRequest = PageRequest.of(source.getNumber(), source.getSize());
        return new PageImpl<T>(newList, pageRequest, source.getTotalElements());
    }

    /**
     * 将一个 Map 对象转化为一个 JavaBean
     *
     * @param obj 要转化的对象
     * @param map 包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     */
    public static Object populate(Map<String, ? extends Object> maps, Object obj) {
        Class cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!maps.containsKey(fields[i].getName())) {
                continue;
            }

            Object values = maps.get(fields[i].getName());
            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(cls, fields[i].getName());
            try {
                descriptor.getWriteMethod().invoke(obj, values);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map<String, Object> (BigDecimal 元会转为Long分,时间转为Long时间戳)
     *
     * @param bean        要转化的JavaBean 对象
     * @param datePattern 日期格式
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map<String, Object> describe(Object bean)
        throws IntrospectionException, IllegalAccessException, InvocationTargetException {

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(bean.getClass());
        Map<String, Object> returnMap = new HashMap();
        for (int i = 0, length = propertyDescriptors.length; i < length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!"class".equals(propertyName)) {
                continue;
            }
            Method readMethod = descriptor.getReadMethod();
            Object result = readMethod.invoke(bean, new Object[0]);
            if (Objects.isNull(result)) {
                returnMap.put(propertyName, null);
                continue;
            }

            if (result instanceof Date) {
                returnMap.put(propertyName, ((Date) result).getTime());
            } else if (result instanceof LocalDate) {
                LocalDate localDate = (LocalDate) result;
                Long time = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                returnMap.put(propertyName, time);
            } else if (result instanceof LocalDateTime) {
                LocalDateTime localDate = (LocalDateTime) result;
                Long time = localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                returnMap.put(propertyName, time);
            } else if (result instanceof List) {
                List tmp = new ArrayList();
                for (Object o : (List) result) {
                    tmp.add(describe(o));
                }
                returnMap.put(propertyName, tmp);
            } else if (result instanceof BigDecimal) {
                BigDecimal newResult = (BigDecimal) result;
                // 元转为分
                Long money = newResult.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).longValue();
                returnMap.put(propertyName, money);
            } else {
                returnMap.put(propertyName, result);
            }
        }
        return returnMap;
    }


    /**
     * 比较两个bean之间的差异
     *
     * @param oldObj
     * @param newObj
     * @return
     */
    public static List<CompareResult> compare(Object oldObj, Object newObj) {
        Class oldCls = oldObj.getClass();
        Class newCls = newObj.getClass();
        if (isBaseDataType(oldCls) || isBaseDataType(newCls)) {
            log.error("所比较的两个类属性及值不能比较");
            return null;
        }
        List<CompareResult> changeItems = new ArrayList<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(oldCls, Object.class);

            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String field = propertyDescriptor.getName();
                // 获取字段旧值
                String oldProp = getValue(propertyDescriptor.getReadMethod().invoke(oldObj));
                // 获取字段新值
                String newProp = getValue(propertyDescriptor.getReadMethod().invoke(newObj));

                // 对比新旧值
                if (!oldProp.equals(newProp)) {
                    CompareResult changeItem = new CompareResult();
                    changeItem.setField(field);
                    changeItem.setNewValue(newProp);
                    changeItem.setOldValue(oldProp);
                    changeItems.add(changeItem);
                }
            }
        } catch (Exception e) {
            log.error("There is error when convert change item", e);
        }
        return changeItems;
    }

    /**
     * 不同类型转字符串的处理
     *
     * @param obj
     * @return
     */
    public static String getValue(Object obj) {
        if (Objects.isNull(obj)) {
            return "";
        }

        if (obj instanceof Date) {
            return formatDateW3C((Date) obj);
        }
        return obj.toString();
    }

    /**
     * 将date类型转为字符串形式
     *
     * @param date
     * @return
     */
    public static String formatDateW3C(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String text = df.format(date);
        return text;
    }

    /**
     * 判断一个类是否为基本数据类型或包装类，或日期。
     *
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    public static boolean isBaseDataType(Object obj) {
        Class clazz = obj.getClass();
        return (obj instanceof Character || clazz.equals(Character.class)) ||
            (obj instanceof Byte || clazz.equals(Byte.class)) ||
            (obj instanceof Boolean || clazz.equals(Boolean.class)) ||
            (obj instanceof Short || clazz.equals(Short.class)) ||
            (obj instanceof Integer || clazz.equals(Integer.class)) ||
            (obj instanceof Long || clazz.equals(Long.class)) ||
            (obj instanceof Float || clazz.equals(Float.class)) ||
            (obj instanceof Double || clazz.equals(Double.class)) ||
            (obj instanceof String || clazz.equals(String.class)) ||
            (obj instanceof BigDecimal || clazz.equals(BigDecimal.class)) ||
            (obj instanceof BigInteger || clazz.equals(BigInteger.class)) ||
            (obj instanceof Date || clazz.equals(Date.class)) ||
            clazz.isPrimitive();
    }
}
