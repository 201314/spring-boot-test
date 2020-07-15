package com.gitee.linzl.commons.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
	 * @param target
	 *            集合B存储的类型
	 * @return
	 */
	public static <T> List<T> copyProperties(Collection<?> source, Class<T> target) {
		return copyProperties(source, target, (String[]) null);
	}

	/**
	 * 把集合A复制到集合B中去,复制相同属性名称,注意属性类型也要一致,忽略ignoreProperties的复制
	 * 
	 * @param source
	 * @param target
	 *            集合B存储的类型
	 * @param ignoreProperties
	 *            忽略ignoreProperties的复制
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
	 * @param target
	 *            集合B存储的类型
	 * @return
	 */
	public static <T> Page<T> copyProperties(Page<?> source, Class<T> target) {
		return copyProperties(source, target, (String[]) null);
	}

	/**
	 * 把集合A复制到集合B中去,复制相同属性名称,注意属性类型也要一致
	 * 
	 * @param source
	 * @param target
	 *            集合B存储的类型
	 * @param ignoreProperties
	 *            忽略ignoreProperties的复制
	 * @return
	 */
	public static <T> Page<T> copyProperties(Page<?> source, Class<T> target, String... ignoreProperties) {
		List<T> newList = copyProperties(source.getContent(), target, ignoreProperties);
		PageRequest pageRequest = PageRequest.of(source.getNumber(), source.getSize());
		Page<T> newPage = new PageImpl<T>(newList, pageRequest, source.getTotalElements());
		return newPage;
	}

}
