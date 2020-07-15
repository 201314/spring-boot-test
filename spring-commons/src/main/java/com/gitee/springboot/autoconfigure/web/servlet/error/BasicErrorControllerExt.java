package com.gitee.springboot.autoconfigure.web.servlet.error;

import com.gitee.linzl.commons.annotation.IgnoreScan;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
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
 * 参考 BasicErrorController修改
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
        return this.errorProperties.getPath();
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        log.error("接口请求错误，http status:【{}】", status);
        Map<String, Object> model = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML));
        response.setStatus(status.value());
        // 进入源码，可知道如果没有找到对应页面，则根据状态码分别指向 4xx,5xx 静态页
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return (modelAndView == null ? new ModelAndView("error", model) : modelAndView);
    }

    @RequestMapping
    public Object error(HttpServletResponse response) {
        ApiResult<Object> rep;
        log.debug("response.getStatus():【{}】", response.getStatus());
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            rep = new ApiResult<>(BaseErrorCode.NOT_FOUND_URL);
        } else {
            rep = new ApiResult<>(BaseErrorCode.MISSING_PARAMETERS);
        }
        return new ResponseEntity<>(rep, HttpStatus.OK);
    }

    /**
     * Determine if the stacktrace attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        IncludeStacktrace include = this.errorProperties.getIncludeStacktrace();
        if (include == IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }
}
