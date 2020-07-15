package com.gitee.linzl.ftp.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.gitee.linzl.boot.autoconfigure.ftp.FtpClientPool;
import com.gitee.linzl.ftp.core.FtpClientTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一注入FtpClientTemplate,使用完成自动回收
 * 
 * 方便开发者专注于写业务代码
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2019年4月8日
 */
@Configuration
@Aspect
@Slf4j
public class AutoFtpClientAspect {
	@Autowired
	private FtpClientPool pool;

	@Around("@annotation(com.gitee.linzl.ftp.annotation.AutoFTPClient)")
	public Object excudeService(ProceedingJoinPoint point) throws Throwable {
//		point.getArgs(); 这个获得的是调用方法时传入的参数值
		Object[] obj = point.getArgs();
		int ftpClientIndex = -1;
		try {
			if (Objects.isNull(obj) || obj.length <= 0) {
				return point.proceed();
			}

			MethodSignature methodPoint = (MethodSignature) point.getSignature();
			Method method = methodPoint.getMethod();
			Parameter[] parametes = method.getParameters();

			int countIndex = 0;
			for (Parameter parameter : parametes) {
				if (FtpClientTemplate.class.equals(parameter.getParameterizedType())) {
					ftpClientIndex = countIndex;
					if (log.isDebugEnabled()) {
						log.debug("参数类型相同,第【{}】个参数为ftp", ftpClientIndex);
					}
					break;
				}
				++countIndex;
			}

			if (ftpClientIndex > -1) {
				obj[ftpClientIndex] = new FtpClientTemplate(pool.borrowObject());
			}
			return point.proceed(obj);
		} catch (Exception t) {
			log.error("AutoFtpClientAspect Throwable:", t);
			// 将异常抛出，由统一异常切面去接收并处理
			throw t;
		} finally {
			if (ftpClientIndex > -1) {
				FtpClientTemplate ftp = (FtpClientTemplate) obj[ftpClientIndex];
				pool.returnObject(ftp.getFtpClient());
			}
		}
	}
}
