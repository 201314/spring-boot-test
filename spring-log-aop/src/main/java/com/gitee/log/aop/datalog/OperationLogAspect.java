package com.gitee.log.aop.datalog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.gitee.log.aop.entity.ChangeItem;
import com.gitee.log.aop.entity.Operation;
import com.gitee.log.aop.entity.OperationType;
import com.gitee.log.aop.util.DiffUtil;

import lombok.extern.slf4j.Slf4j;

// @Aspect
@Component
@Slf4j
public class OperationLogAspect {
	@Autowired
	private OperationLogDao actionDao;
	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private MybatisPlusProperties properties;

	@Pointcut("execution(* com.gitee.log.aop.dao.*.insert*(..)) " + "|| execution(* com.gitee.log.aop.dao.*.save*(..)) "
			+ "|| execution(* com.gitee.log.aop.dao.*.update*(..))")
	public void save() {
		log.debug("save切面");
	}

	@Pointcut("execution(* com.gitee.log.aop.dao.*.delete*(..))"
			+ "|| execution(* com.gitee.log.aop.dao.*.remove*(..))")
	public void delete() {
		log.debug("delete切面");
	}

	/**
	 * 1\判断是什么类型的操作,增加\删除\还是更新 增加/更新 save(Product),通过id区分是增加还是更新 删除delete(id)
	 * 2\获取changeitem
	 * 
	 * (1)新增操作,before直接获取,after记录下新增之后的id
	 * 
	 * (2)更新操作,before获取操作之前的记录,after获取操作之后的记录,然后diff
	 * 
	 * (3)删除操作,before根据id取记录
	 * 
	 * 3\保存操作记录 actionType objectId objectClass
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("save() || delete()")
	public Object addOperateLog(ProceedingJoinPoint pjp) throws Throwable {
		Operation action = new Operation();
		OperationType actionType = null;
		Long id = null;
		Object oldObj = null;
		Object returnObj = null;
		Object requestId = null;

		try {
			Object obj = pjp.getArgs()[0];
			String method = pjp.getSignature().getName();
			if (method.startsWith("save") || method.startsWith("insert")) {
				try {
					// BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
					// PropertyDescriptor[] propertyDescriptor = beanInfo.getPropertyDescriptors();
					// PropertyDescriptor target = null;
					// for (PropertyDescriptor pd : propertyDescriptor) {
					// if ("id".equals(pd.getName())) {
					// target = pd;
					// break;
					// }
					// }
				} catch (Exception e) {
					log.error("出错:", e);
				}
				actionType = OperationType.INSERT;
				List<ChangeItem> changeItems = DiffUtil.getInsertChangeItems(obj);
				action.getChanges().addAll(changeItems);
				action.setObjectClass(obj.getClass().getName());

			} else if (method.startsWith("update")) {
				actionType = OperationType.UPDATE;
				requestId = PropertyUtils.getProperty(obj, getPKField(obj.getClass()));
				id = Long.valueOf(requestId.toString());
				action.setObjectId(id);

				Signature sig = pjp.getSignature();
				if (!(sig instanceof MethodSignature)) {
					throw new IllegalArgumentException("该注解只能用于方法");
				}
				MethodSignature msig = (MethodSignature) sig;

				Class<?> mapper = msig.getMethod().getDeclaringClass();
				Object target = pjp.getTarget();
				Object proxy = sqlSession.getMapper(msig.getMethod().getDeclaringClass());
				System.out.println("properties==="
						+ properties.getConfiguration().getMapperRegistry().getMapper(mapper, sqlSession));
				System.out.println("mapper==" + mapper);
				System.out.println("proxy==" + proxy);
				System.out.println("target==" + target);

				MybatisMapperProxy mybatisProxy = (MybatisMapperProxy) proxy;

				Method findMethod = msig.getMethod().getDeclaringClass().getMethod("selectById", Long.class);
				System.out.println("查询结果：" + mybatisProxy.invoke(mybatisProxy, findMethod, new Object[] { id }));
				oldObj = DiffUtil.getObjectById(proxy, mapper, id);
				action.setObjectClass(oldObj.getClass().getName());

			} else if (method.startsWith("delete") || method.startsWith("remove")) {
				actionType = OperationType.DELETE;
				id = Long.valueOf(obj.toString());
				oldObj = DiffUtil.getObjectById(pjp.getTarget(), id);
				ChangeItem changeItem = DiffUtil.getDeleteChangeItem(oldObj);
				action.getChanges().add(changeItem);
				action.setObjectId(id);
				action.setObjectClass(oldObj.getClass().getName());
			}

			action.setActionType(actionType);
			if (OperationType.INSERT == actionType) {
				returnObj = pjp.proceed(pjp.getArgs());

				Signature sig = pjp.getSignature();
				MethodSignature msig = null;
				if (!(sig instanceof MethodSignature)) {
					throw new IllegalArgumentException("该注解只能用于方法");
				}
				msig = (MethodSignature) sig;
				Object target = pjp.getTarget();
				Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
				if (currentMethod.getReturnType().equals(void.class)

						|| isBaseType(currentMethod.getReturnType())

						|| isBaseWrapperType(currentMethod.getReturnType())) {
					// returnObj 返回成功数量
				} else {
					returnObj = obj;
					// new id
					Object newId = PropertyUtils.getProperty(returnObj,
							TableInfoHelper.getTableInfo(obj.getClass()).getKeyProperty());
					action.setObjectId(Long.valueOf(String.valueOf(newId)));
				}
			} else if (OperationType.UPDATE == actionType) {
				Object newObj = DiffUtil.getObjectById(pjp.getTarget(), id);
				List<ChangeItem> changeItems = DiffUtil.getChangeItems(oldObj, newObj);
				action.getChanges().addAll(changeItems);
			}

			action.setOperator("admin"); // dynamic get from threadlocal/session
			action.setOperateTime(new Date());

			log.debug("输出操作:【{}】", JSON.toJSONString(action));
			// actionDao.save(action);
		} catch (Exception e) {
			log.error("异常:", e);
		}
		return returnObj;
	}

	/**
	 * 判断object是否为基本类型
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isBaseWrapperType(Object object) {
		Class className = object.getClass();
		if (Integer.class.equals(className) || Byte.class.equals(className)

				|| Long.class.equals(className) || Double.class.equals(className)

				|| Float.class.equals(className) || Character.class.equals(className)

				|| Short.class.equals(className) || Boolean.class.equals(className)) {
			return true;
		}
		return false;
	}

	public static boolean isBaseType(Object object) throws NoSuchFieldException, SecurityException {
		if (object == int.class || object == byte.class || object == long.class

				|| object == double.class || object == float.class || object == char.class

				|| object == short.class || object == boolean.class) {
			return true;
		}
		return false;
	}

	public static String getPKField(Class<?> clazz) {
		List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
		if (CollectionUtils.isNotEmpty(fieldList)) {
			return fieldList.stream().filter(i -> {
				/* 过滤注解主键字段属性 */
				return i.isAnnotationPresent(TableId.class);
			}).collect(Collectors.toList()).get(0).getName();
		}
		return fieldList.get(0).getName();
	}
}
