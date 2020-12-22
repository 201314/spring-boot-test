package com.gitee.linzl.commons.aop;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 控制层校验器
 *
 * @author linzhenlie
 * @date 2019/9/2
 */
@Configuration
@Aspect
@Slf4j
public class ControllerValidatorAspect {
    @Pointcut("within(@org.springframework.stereotype.Controller *)||within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerExpression() {
    }


    @Around("controllerExpression()&& args(..,bindingResult)")
    public Object doAround(ProceedingJoinPoint pjp, BindingResult bindingResult) throws Throwable {
        LocalDateTime requestTime = LocalDateTime.now();
        Object retVal;
        if (Objects.nonNull(bindingResult) && bindingResult.hasErrors()) {
            retVal = getValidResult(bindingResult);
        } else {
            try {
                retVal = pjp.proceed();
            } catch (Throwable t) {
                throw t;
            }
        }
        commonColumn(retVal, requestTime);
        return retVal;
    }


    @Around("controllerExpression()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        LocalDateTime requestTime = LocalDateTime.now();
        Object retVal = pjp.proceed();
        commonColumn(retVal, requestTime);
        return retVal;
    }


    private ApiResult getValidResult(BindingResult bindingResult) {
        List<FieldError> errorList = bindingResult.getFieldErrors();
        if (!CollectionUtils.isEmpty(errorList)) {
            errorList.stream().forEach((fe) -> {
                log.error("参数校验不通过,字段:【{}】,校验提醒:【{}】,类名:【{}】", fe.getField(), fe.getDefaultMessage(),fe);
            });
        }
        return ApiResults.fail(BaseErrorCode.INVALID_PARAMETERS);
    }

    private void commonColumn(Object retVal, LocalDateTime requestTime) {
        if (retVal instanceof ApiResult) {
            ApiResult result = (ApiResult) retVal;
            result.setRequestTime(requestTime);
            result.setResponseTime(LocalDateTime.now());
        }
    }
}
