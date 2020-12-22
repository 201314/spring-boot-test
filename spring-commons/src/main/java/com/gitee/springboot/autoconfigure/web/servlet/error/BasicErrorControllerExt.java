package com.gitee.springboot.autoconfigure.web.servlet.error;

import com.gitee.linzl.commons.annotation.IgnoreScan;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * BasicErrorControllerExt不能被自定义扫描Controller扫描到，否则无法启动
 * <p>
 * 参考 org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController修改
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2018年8月2日
 */
@Lazy
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
@Slf4j
@IgnoreScan
public class BasicErrorControllerExt extends AbstractErrorController {
    private final ErrorProperties errorProperties;

    public BasicErrorControllerExt(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
                                   List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        Assert.notNull(errorProperties, "ErrorProperties must not be null");
        this.errorProperties = errorProperties;
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        log.error("接口请求错误，http status:【{}】", status);

        Map<String, Object> model = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML));
        response.setStatus(status.value());
        // 进入源码，可知道如果没有找到对应页面，则根据状态码分别指向 4xx,5xx 静态页
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return (modelAndView != null ? modelAndView : new ModelAndView("error", model));
    }

    @RequestMapping
    public Object error(HttpServletResponse response) {
        ApiResult<Object> rep;
        log.debug("response.getStatus():【{}】", response.getStatus());
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            rep = ApiResults.fail(BaseErrorCode.NOT_FOUND_URL);
        } else {
            rep = ApiResults.fail(BaseErrorCode.MISSING_PARAMETERS);
        }
        return new ResponseEntity<>(rep, HttpStatus.OK);
    }

    protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        if (this.errorProperties.isIncludeException()) {
            options = options.including(ErrorAttributeOptions.Include.EXCEPTION);
        }
        if (isIncludeStackTrace(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }
        if (isIncludeMessage(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.MESSAGE);
        }
        if (isIncludeBindingErrors(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.BINDING_ERRORS);
        }
        return options;
    }

    /**
     * Determine if the stacktrace attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    @SuppressWarnings("deprecation")
    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        switch (getErrorProperties().getIncludeStacktrace()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
            case ON_TRACE_PARAM:
                return getTraceParameter(request);
            default:
                return false;
        }
    }

    /**
     * Determine if the message attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the message attribute should be included
     */
    protected boolean isIncludeMessage(HttpServletRequest request, MediaType produces) {
        switch (getErrorProperties().getIncludeMessage()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return getMessageParameter(request);
            default:
                return false;
        }
    }

    /**
     * Determine if the errors attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the errors attribute should be included
     */
    protected boolean isIncludeBindingErrors(HttpServletRequest request, MediaType produces) {
        switch (getErrorProperties().getIncludeBindingErrors()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return getErrorsParameter(request);
            default:
                return false;
        }
    }

    /**
     * Provide access to the error properties.
     *
     * @return the error properties
     */
    protected ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }
}
