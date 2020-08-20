package com.gitee.linzl.commons.aop;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.exception.BaseException;
import com.gitee.linzl.commons.exception.BusinessException;
import com.gitee.linzl.commons.tools.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

/**
 * 针对所有控制器进行异常处理,使用注解更灵活，不同的异常不同的方法
 * <p>
 * 这个只能捕获取控制器的异常，
 *
 * @author linzl
 */
//@RestControllerAdvice(basePackages = "com.gitee.linzl")
@ControllerAdvice(basePackages = "com.gitee.linzl")
@Slf4j
public class GlobalExceptionAdvice {
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpServletRequest request, HttpServletResponse response, HttpMessageNotReadableException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,参数解析失败:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(),
                    e);
        }
        return ApiResult.fail(BaseErrorCode.BAD_REQUEST);
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ValidationException.class)
    public Object handleValidationException(HttpServletRequest request, HttpServletResponse response, ValidationException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,参数校验失败:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(),
                    e);
        }
        return ApiResult.fail(BaseErrorCode.INVALID_PARAMETERS);
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,参数验证失败:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(),
                    e);
        }
        return ApiResult.fail(BaseErrorCode.INVALID_PARAMETERS);
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Object handleException(HttpServletRequest request, HttpServletResponse response, HttpRequestMethodNotSupportedException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,方法不支持:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(),
                    e);
        }
        return ApiResult.fail(BaseErrorCode.NOT_FOUND_URL);
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Object handleHttpMediaTypeNotSupportedException(HttpServletRequest request, HttpServletResponse response, HttpMediaTypeNotSupportedException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,不支持当前媒体类型:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(),
                    e);
        }
        return ApiResult.fail(BaseErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageConversionException.class)
    public Object messageConversion(HttpServletRequest request, HttpServletResponse response,
                                    HttpMessageConversionException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,传参格式错误，是否使用json或表单:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(), e);
        }
        if (ServletUtil.isAjax(request)) {
            return ApiResult.fail(BaseErrorCode.INVALID_PARAMETERS);
        }
        // 同步
        ModelAndView model = new ModelAndView();
        model.setViewName("error/5xx");
        model.addObject("error", e.getMessage());
        return model;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = BaseException.class)
    public Object diyException(HttpServletRequest request, HttpServletResponse response, BaseException e) {
        if (log.isErrorEnabled()) {
            log.error("自定义Exception全局异常捕捉:", e);
        }
        if (ServletUtil.isAjax(request)) {
            return new ApiResult(BaseErrorCode.SYS_ERROR);
        }
        // 同步
        ModelAndView model = new ModelAndView();
        model.setViewName("error/5xx");
        model.addObject("error", e.getMessage());
        return model;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public Object businessException(HttpServletRequest request, HttpServletResponse response, BusinessException e) {
        if (log.isErrorEnabled()) {
            log.error("url:【{}】,服务器IP:【{}】,业务异常错误信息:{}", request.getRequestURL(), request.getRemoteAddr(), e.getMessage(), e);
        }
        return ApiResult.fail(e.getErrorEnum());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public Object sysException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (log.isErrorEnabled()) {
            log.error("系统Exception全局异常捕捉:", e);
        }
        if (ServletUtil.isAjax(request)) {
            return ApiResult.fail();
        }
        // 同步
        ModelAndView model = new ModelAndView();
        model.setViewName("error/5xx");
        model.addObject("error", e.getMessage());
        return model;
    }
}