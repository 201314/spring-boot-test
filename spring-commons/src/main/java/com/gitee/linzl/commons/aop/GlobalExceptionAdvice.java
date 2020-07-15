package com.gitee.linzl.commons.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gitee.linzl.commons.exception.BaseException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.tools.ServletUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 针对所有控制器进行异常处理,使用注解更灵活，不同的异常不同的方法
 * <p>
 * 这个只能捕获取控制器的异常，
 *
 * @author linzl
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {
	@ExceptionHandler(HttpMessageConversionException.class)
	public Object messageConversion(HttpServletRequest request, HttpServletResponse response,
			HttpMessageConversionException exception) {
		if (log.isDebugEnabled()) {
			log.error("传参格式错误，是否使用json或表单:", exception);
		}
		if (ServletUtil.isAjax(request)) {
			return  ApiResult.fail(BaseErrorCode.INVALID_PARAMETERS);
		}
		// 同步
		ModelAndView model = new ModelAndView();
		model.setViewName("error/5xx");
		model.addObject("error", exception.getMessage());
		return model;

	}

	@ExceptionHandler(value = Exception.class)
	public Object sysException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		if (log.isDebugEnabled()) {
			log.error("系统Exception全局异常捕捉:", exception);
		}
		if (ServletUtil.isAjax(request)) {
			return ApiResult.fail();
		}
		// 同步
		ModelAndView model = new ModelAndView();
		model.setViewName("error/5xx");
		model.addObject("error", exception.getMessage());
		return model;
	}

	@ExceptionHandler(value = BaseException.class)
	public Object diyException(HttpServletRequest request, HttpServletResponse response, BaseException exception) {
		if (log.isDebugEnabled()) {
			log.error("自定义Exception全局异常捕捉:", exception);
		}
		if (ServletUtil.isAjax(request)) {
			return new ApiResult(BaseErrorCode.SYS_ERROR);
		}
		// 同步
		ModelAndView model = new ModelAndView();
		model.setViewName("error/5xx");
		model.addObject("error", exception.getMessage());
		return model;
	}

}